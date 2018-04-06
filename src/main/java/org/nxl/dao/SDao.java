package org.nxl.dao;

import lombok.extern.slf4j.Slf4j;
import org.nxl.SqlMapper.EntitySql;
import org.nxl.SqlMapper.SqlMapper;
import org.nxl.annotation.Entity;
import org.nxl.annotation.PK;
import org.nxl.exception.SDaoException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SDao {

    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private JdbcTemplate jdbcTemplateMaster;
    private JdbcTemplate jdbcTemplateSlave;
    private SqlMapper sqlMapper;

    private List<Class> basicTypes = Arrays.asList(new Class[]{Boolean.class,Float.class,Short.class,Integer.class,Long.class,Double.class,String.class});

    public SDao(DataSource dataSourceMaster) {
        this.dataSourceMaster = dataSourceMaster;
        this.dataSourceSlave = dataSourceMaster;
        jdbcTemplateMaster = new JdbcTemplate(this.dataSourceMaster);
        jdbcTemplateSlave = new JdbcTemplate(dataSourceSlave);
    }

    public SDao(DataSource dataSourceMaster, DataSource dataSourceSlave) {
        this.dataSourceMaster = dataSourceMaster;
        this.dataSourceSlave = dataSourceSlave;
        jdbcTemplateMaster = new JdbcTemplate(this.dataSourceMaster);
        jdbcTemplateSlave = new JdbcTemplate(this.dataSourceSlave);
    }

    public void setSqlMapper(String packagePath) {
        this.sqlMapper = new SqlMapper(packagePath);
    }

    /**
     *
     * @param sql
     * @param params
     * @return
     */
    public int executeSql(String sql,Object... params){
        return jdbcTemplateMaster.update(sql, params);
    }

    /**
     * 获取唯一结果（超出一条将抛错）
     * @param sql
     * @param t
     * @param params
     * @param <T>
     * @return
     */
    public <T> T uniqueOne(String sql,Class<T> t,Object... params){
        if(basicTypes.contains(t)){
            return jdbcTemplateSlave.queryForObject(sql, t, params);
        }
        return jdbcTemplateSlave.queryForObject(sql, new BeanPropertyRowMapper<T>(t), params);
    }

    /**
     * 获取第一条结果
     * @param sql
     * @param t
     * @param params
     * @param <T>
     * @return
     */
    public <T> T first(String sql,Class<T> t,Object... params){
        sql = sql + " limit 1";
        if(basicTypes.contains(t)){
            return jdbcTemplateSlave.queryForObject(sql, t, params);
        }
        return jdbcTemplateSlave.queryForObject(sql, new BeanPropertyRowMapper<T>(t), params);
    }

    /**
     * 获取列表数据
     * @param sql
     * @param t
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> list(String sql, Class<T> t, Object... params){
        if(basicTypes.contains(t)){
            return jdbcTemplateSlave.queryForList(sql, t, params);
        }
        return jdbcTemplateSlave.query(sql, new BeanPropertyRowMapper<T>(t), params);
    }

    public<T> int save(T t) throws IllegalAccessException {
        EntitySql entitySql = sqlMapper.getMapper().get(t.getClass());
        if(t.getClass().getAnnotation(Entity.class) == null){
            throw new SDaoException("只能保存有@Entity注解的类对象");
        }
        Field[] fields = t.getClass().getDeclaredFields();
        List<Object> values = new LinkedList<>();
        for(Field field : fields){
            field.setAccessible(true);
            Object value = field.get(t);
            values.add(value);
        }
        return jdbcTemplateMaster.update(entitySql.getInsertSql(),values.toArray());
    }

    public<T> int update(T t) throws IllegalAccessException {
        EntitySql entitySql = sqlMapper.getMapper().get(t.getClass());
        if(t.getClass().getAnnotation(Entity.class) == null){
            throw new SDaoException("只能修改有@Entity注解的类对象");
        }
        Field[] fields = t.getClass().getDeclaredFields();
        List<Object> values = new LinkedList<>();
        Field PkField = null;
        for(Field field : fields){
            if(field.getAnnotation(PK.class) != null){
                PkField = field;
                PkField.setAccessible(true);
            }
            field.setAccessible(true);
            Object value = field.get(t);
            values.add(value);
        }
        if(PkField == null){
            throw new SDaoException("找不到Entity实体的主键PK");
        }
        values.add(PkField.get(t));
        return jdbcTemplateMaster.update(entitySql.getUpdateSql(),values.toArray());
    }

    public<T> void delete(T t) throws IllegalAccessException {
        EntitySql entitySql = sqlMapper.getMapper().get(t.getClass());
        if(t.getClass().getAnnotation(Entity.class) == null){
            throw new SDaoException("只能删除有@Entity注解的类对象");
        }
        Field[] fields = t.getClass().getDeclaredFields();
        Field PkField = null;
        for(Field field : fields){
            if(field.getAnnotation(PK.class) != null){
                PkField = field;
                PkField.setAccessible(true);
                break;
            }
        }
        if(PkField == null){
            throw new SDaoException("找不到Entity实体的主键PK");
        }
        String sql = entitySql.getDeleteSql();
        log.info("sql = "+sql);
        jdbcTemplateMaster.update(entitySql.getDeleteSql(),PkField.get(t));
    }

}
