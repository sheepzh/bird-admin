package com.bird.helper;

import com.bird.dao.SystemSequenceMapper;
import com.bird.model.entity.SystemSequence;
import com.bird.utils.DateUtil;
import com.bird.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.toUpperCase;

/**
 * 流水号工具
 *
 * @author zhyyy
 **/
@Component
public class SequenceHelper {
    private final Map<Class, String> CLASS_KEY_POOL = new HashMap<>(128);

    private final SystemSequenceMapper systemSequenceMapper;
    private final static String ERROR_MESSAGE = "生成流水号错误";

    @Autowired
    public SequenceHelper(SystemSequenceMapper systemSequenceMapper) {
        this.systemSequenceMapper = systemSequenceMapper;
    }

    /**
     * 生成流水号
     * 表名前两单词首字母（只有一个单词则为前两个字母）+列名前第一字母+yymmdd+sequence（5位）+时间戳个位数 = 15位
     *
     * @param entityClass 类
     * @param fieldName   列名
     * @return 流水号
     */
    public String generate(Class<?> entityClass, String fieldName) {
        int minLength = 2;
        String clzName = entityClass.getSimpleName();

        int sequence = getAndAdd(clzName, fieldName);

        String prefix = CLASS_KEY_POOL.get(entityClass);
        if (prefix == null) {
            StringBuilder res = new StringBuilder().append(clzName.charAt(0));
            for (int i = 1; i < clzName.length() && res.length() < minLength; i++) {
                if (Character.isUpperCase(clzName.charAt(i))) {
                    res.append(clzName.charAt(i));
                }
            }
            if (res.length() == 1 && clzName.length() > 1) {
                res.append(Character.toUpperCase(clzName.charAt(1)));
            }
            prefix = res.toString();
            CLASS_KEY_POOL.put(entityClass, prefix);
        }

        return prefix +
                toUpperCase(fieldName.charAt(0)) +
                DateUtil.formatDate(new Date(), "yyMMdd") +
                StringUtil.alignZeroToLength(sequence, 5, true) +
                System.currentTimeMillis() % 10;
    }

    /**
     * 生成实体内部标识号（无日期）
     * 表名前两字母+列名前第一字母+sequence（5位）+时间戳个位数 = 9位
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 内部标识
     */
    public String generateId(String tableName, String columnName) {
        if (StringUtil.isBlank(tableName) || StringUtil.isBlank(columnName)) {
            throw new RuntimeException(ERROR_MESSAGE);
        }
        int sequence = getAndAdd(tableName, columnName);
        return String.valueOf(tableName.substring(0, 2).toUpperCase()) +
                toUpperCase(columnName.charAt(0)) +
                StringUtil.alignZeroToLength(sequence, 5, true) +
                System.currentTimeMillis() % 10;
    }

    /**
     * 可使用存储过程或者缓存
     *
     * @param entityName 实体名
     * @param fieldName  属性名
     * @return sequence
     * @see com.bird.common.cache.SimpleKvCache#casInc(String)
     */
    private synchronized Integer getAndAdd(String entityName, String fieldName) {
        SystemSequence key = new SystemSequence();
        key.setTableName(entityName.toUpperCase());
        key.setColumnName(fieldName.toUpperCase());
        SystemSequence sequence = systemSequenceMapper.selectByPrimaryKey(key);
        if (sequence == null) {
            sequence = key;
            sequence.setSequence(2);
            systemSequenceMapper.insert(sequence);
            return 1;
        } else {
            sequence.setSequence(sequence.getSequence() + 1);
            systemSequenceMapper.updateByPrimaryKey(sequence);
            return sequence.getSequence();
        }
    }
}
