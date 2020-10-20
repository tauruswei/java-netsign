package com.brilliance.netsign.dao;

import com.brilliance.netsign.entity.Key;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * 密钥(Key)表数据库访问层
 *
 * @author makejava
 * @since 2020-09-07 11:23:18
 */
@Mapper
public interface KeyDao {

    /**
     * 通过ID查询单条数据
     *
     * @param  主键
     * @return 实例对象
     */
    @Select("select key_label, key_material , key_enable from netsign.key where key_label = #{keyLabel}")
    @Results({
            @Result(column ="key_label",property ="keyLabel",jdbcType = JdbcType.VARCHAR),
            @Result(column ="key_material",property ="keyMaterial",jdbcType = JdbcType.VARCHAR),
            @Result(column ="key_enable",property ="keyEnable",jdbcType = JdbcType.INTEGER)
    })
    Key queryById(String keyLabel);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Key> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param key 实例对象
     * @return 对象列表
     */
    List<Key> queryAll(Key key);

    /**
     * 新增数据
     *
     * @param key 实例对象
     * @return 影响行数
     */
    @Insert("insert into netsign.key(key_label,key_material)  values (#{keyLabel},#{keyMaterial})")
    int insert(Key key);

    /**
     * 修改数据
     *
     * @return 影响行数
     */
    @Update("update netsign.key set key_enable = 1 where binary(key_label)= #{keylabel}")
    int update(String keyLabel);

    /**
     * 通过主键删除数据
     *
     * @param  主键
     * @return 影响行数
     */
    @Delete("delete from netsign.key where  key_label= #{keyLabel}")
    int deleteById(String keyLabel);

}