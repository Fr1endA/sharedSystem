package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("hibernates")
public class AlphaHibernates implements AlphaDao{
    @Override
    public String select() {
        return "Hibernates";
    }
}
