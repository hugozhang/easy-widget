package me.about.widget.mybatis.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分批处理
 *
 * @author: hugo.zxh
 * @date: 2022/05/10 22:52
 * @description:
 */
public class BatchFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFunction.class);

    public static <T,D> void batchFunction(List<T> rows, int batchSize, Function<List<T>,D> ...functions) {
        if (rows == null || rows.isEmpty() || functions == null) {
            return;
        }
        List<T> tmp = new ArrayList<>();
        for (int i = 0,len = rows.size(); i < len; i++ ) {
            tmp.add(rows.get(i));
            if (tmp.size() == batchSize || i == rows.size() - 1) {
                for (Function function : functions) {
                    function.apply(tmp);
                    LOGGER.debug("批次总量：{}，每次处理量：{}，当前处理量：{}。",new Object[]{rows.size(),batchSize,i});
                }
                tmp.clear();
            }
        }
    }
}
