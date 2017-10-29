package com.example.demo.service;

import com.example.demo.core.APIMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created by wangyonghua on 2017/10/28.
 */
@Service
public class GoodService {

    @APIMapper("com.example.addGoods")
    public Goods addGoods(Goods goods, Integer id) {

        return goods;
    }
}


