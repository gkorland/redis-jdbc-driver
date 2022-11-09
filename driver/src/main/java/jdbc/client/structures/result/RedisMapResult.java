package jdbc.client.structures.result;

import jdbc.client.structures.query.RedisQuery;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RedisMapResult extends RedisResultBase<String, Map<String, Object>> {
    public RedisMapResult(@NotNull RedisQuery query,
                          @NotNull String type,
                          @NotNull Map<String, Object> result) {
        super(query, type, result);
    }
}
