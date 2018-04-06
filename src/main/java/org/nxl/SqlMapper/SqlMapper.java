package org.nxl.SqlMapper;

import org.nxl.annotation.Column;
import org.nxl.annotation.Entity;
import org.nxl.annotation.PK;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity对象的DDL语句管理
 */
public class SqlMapper {

    private String packagePath;
    private Map<Class,EntitySql> mapper = new HashMap<>();

    public SqlMapper(String packagePath) {
        this.packagePath = packagePath;
        entityInit();
    }

    public Map<Class, EntitySql> getMapper() {
        return mapper;
    }

    /**
     * 初始化Entity与数据库之间的信息
     */
    private void entityInit(){
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String basePackage = packagePath;
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/**/*.class";
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(new PathMatchingResourcePatternResolver());
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);
                // 解析注解
                Entity entity = clazz.getAnnotation(Entity.class);
                if(entity == null) continue;
                EntitySql entitySql = new EntitySql();
                // 解析表名
                String tableName = entity.name();
                if(StringUtils.isEmpty(tableName)){
                    tableName = clazz.getSimpleName();
                }
                // 解析colnum及PK
                Field[] declaredFields = clazz.getDeclaredFields();
                StringBuilder insertSql = new StringBuilder("insert into ").append(tableName).append(" (");
                StringBuilder updateSql = new StringBuilder("update ").append(tableName).append(" set ");
                StringBuilder deleteSql = new StringBuilder("delete from ").append(tableName);
                StringBuilder placeholder = new StringBuilder();
                Field pkField = null;
                for(Field field : declaredFields){
                    Column column = field.getAnnotation(Column.class);
                    PK pk = field.getAnnotation(PK.class);
                    if(pk != null){
                        pkField = field;
                    }
                    if(column == null) continue;
                    String colnumName = column.name();
                    if(StringUtils.isEmpty(colnumName)){
                        colnumName = field.getName();
                    }
                    insertSql.append(colnumName).append(",");
                    updateSql.append(colnumName).append("=").append("?").append(",");
                    placeholder.append("?,");
                }
                updateSql.deleteCharAt(updateSql.length()-1);
                if(pkField != null){
                    updateSql.append(" where ").append(pkField.getName()).append("=?");
                    deleteSql.append(" where ").append(pkField.getName()).append("=?");
                    entitySql.setUpdateSql(updateSql.toString());
                    entitySql.setDeleteSql(deleteSql.toString());
                }
                placeholder.deleteCharAt(placeholder.length()-1);
                insertSql.replace(insertSql.length()-1,insertSql.length(),")");
                insertSql.append(" values (").append(placeholder).append(")");
                entitySql.setInsertSql(insertSql.toString());
                mapper.put(clazz, entitySql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
