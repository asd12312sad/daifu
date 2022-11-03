package com.ms.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.common.domain.RequestLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequestLogDao   {
    @Insert("insert into request_log(name,merchant_id,request_log,response_log,type,merchant_user_id,status,merchant_user_name,request_time,ip) values(#{name},#{merchantId},#{requestLog},#{responseLog},#{type},#{merchantUserId},#{status},(select merchant_user.user_name from merchant_user where merchant_user.user_id  = #{merchantUserId} LIMIT 1),#{requestTime},#{ip})")
    int insert(RequestLog record);
}
