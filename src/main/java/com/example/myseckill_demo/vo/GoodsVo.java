package com.example.myseckill_demo.vo;

import com.example.myseckill_demo.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods {

    /**
     * 秒杀价格
     **/

    private BigDecimal seckillPrice;

    /**
     * 剩余数量
     **/

    private Integer stockCount;

    /**
     * 开始时间
     **/

    private Date startDate;

    /**
     * 结束时间
     **/

    private Date endDate;


}
