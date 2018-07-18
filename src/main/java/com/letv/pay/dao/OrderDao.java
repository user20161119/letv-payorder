package com.letv.pay.dao;

import java.util.List;

import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;
import org.jfaster.mango.annotation.Sharding;
import org.jfaster.mango.annotation.ShardingBy;
import org.jfaster.mango.sharding.ShardingStrategy;

import com.letv.pay.pojo.Order;
import com.letv.pay.util.ShardUtil;

/**
 * @author ash
 */
@DB(table = "t_order")
public interface OrderDao {

    final String COLUMNS = "id, user_id, price, status";

    @SQL("insert into #table(" + COLUMNS + ") values(:id, :userId, :price, :status)")
    @Sharding(shardingStrategy = OrderIdShardingStrategy.class)
    void addOrder(@ShardingBy("id") Order order);

    @SQL("select " + COLUMNS + " from #table where id = :1")
    @Sharding(shardingStrategy = OrderIdShardingStrategy.class)
    Order getOrderById(@ShardingBy String id);

    @SQL("select " + COLUMNS + " from #table where user_id = :1")
    @Sharding(shardingStrategy = UserIdShardingStrategy.class)
    List<Order> getOrdersByUserId(@ShardingBy int userId);

    class OrderIdShardingStrategy implements ShardingStrategy<String, String> {

        @Override
        public String getDataSourceFactoryName(String orderId) {
            return "dsf" + orderId.substring(0, 1);
        }

        @Override
        public String getTargetTable(String table, String orderId) {
            return table + "_" + orderId.substring(1, 2);
        }

    }

    class UserIdShardingStrategy implements ShardingStrategy<Integer, Integer> {

        @Override
        public String getDataSourceFactoryName(Integer userId) {
            return "dsf" + ShardUtil.getDBInfoByUserId(userId);
        }

        @Override
        public String getTargetTable(String table, Integer userId) {
            return table + "_" + (ShardUtil.getTableInfoByUserId(userId));
        }
    }

}





