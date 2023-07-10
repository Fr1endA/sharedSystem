package com.nowcoder.community.until;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    //前缀树
    //用map存储子节点，key为子字符，value为下一个结点————————生成多叉树
    private class TrieNode{
        //结束标识符
        private boolean isKeywordEnd=false;
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //子节点:key: 下级结点字符，value是下级节点
        private Map<Character,TrieNode> subNodes=new HashMap<>();
        //添加子节点：
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }

    //替换符号
    private static final String REPLACEMENT="***";

    //根节点
    private TrieNode rootNode =new TrieNode();

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点,进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    @PostConstruct
    public void init(){

        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode=rootNode;
        int begin=0;
        int position=begin;

        StringBuilder sb=new StringBuilder();

        while(position<text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                //不是敏感词，直接添加
                sb.append(c);
                position=++begin;
                tempNode=rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词
                sb.append(REPLACEMENT);
                begin=++position;
                tempNode=rootNode;
            }else{

                position++;
            }

        }

        sb.append(text.substring(begin));
        return sb.toString();
    }
    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }



}
