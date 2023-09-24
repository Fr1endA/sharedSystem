package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)


public class AlgorithmTest {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    @Test
    public void leetcode() {
        class Solution {
            List<List<Integer>> result = new ArrayList<>();
            List<Integer> path = new ArrayList<>();
            public List<List<Integer>> combinationSum(int[] candidates, int target) {

                Arrays.sort(candidates);
                backtracking(candidates, target, 0, 0);
                return result;
            }
            private void backtracking(int[] candidates, int target, int sum, int startIndex){

                if(sum==target){
                    result.add(new ArrayList<>(path));
                    return;
                }

                for(int i = startIndex; i < path.size(); i++){

                    sum+=candidates[i];
                    if(sum>target){
                        break;
                    }
                    path.add(candidates[i]);
                    backtracking(candidates, target, sum, i+1);
                    sum-=candidates[i];
                    path.remove(path.size()-1);

                }

            }
        }
//        class Solution {
//            List<List<Integer>> result = new ArrayList<>();
//            LinkedList<Integer> path = new LinkedList<>();
//
//            public List<List<Integer>> combinationSum3(int k, int n) {
//                backTracking(n, k, 1, 0);
//                return result;
//            }
//
//            private void backTracking(int targetSum, int k, int startIndex, int sum) {
//                // 减枝
//                if (sum > targetSum) {
//                    return;
//                }
//
//                if (path.size() == k) {
//                    if (sum == targetSum) result.add(new ArrayList<>(path));
//                    return;
//                }
//
//                // 减枝 9 - (k - path.size()) + 1
//                for (int i = startIndex; i <= 9 - (k - path.size()) + 1; i++) {
//                    path.add(i);
//                    sum += i;
//                    backTracking(targetSum, k, i + 1, sum);
//                    //回溯
//                    path.removeLast();
//                    //回溯
//                    sum -= i;
//                }
//            }
//        }

        Solution solution=new Solution();

        System.out.println(solution.combinationSum(new int[]{1,2,6,3,4},3));



    }
}


