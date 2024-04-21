package com.qyd.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.service.user.repository.entity.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-18 22:16
 */
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 更具第三方唯一id,进行查询
     *
     * @param accountId
     * @return
     */
    @Select("select * from user where third_account_id = #{account_id} limit 1")
    UserDO getByThirdAccountId(@Param("account_id") String accountId);

    /**
     * 遍历用户id
     * todo: 这个方法作用存疑
     *
     * @param offsetUserId
     * @param size
     * @return
     */
    @Select("select id from user where id > #{offsetUserId} order by id asc limit #{size}}")
    List<Long> getUserIdsOrderByIdAsc(@Param("offsetUserId") Long offsetUserId, @Param("size") Long size);
}
