package jdbc.client.helpers.query;

import jdbc.client.structures.query.RedisQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Protocol.Command;
import redis.clients.jedis.Protocol.Keyword;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisQueryHelper {

    private RedisQueryHelper() {
    }

    private static final Map<String, Command> COMMANDS =
            Arrays.stream(Command.values()).collect(Collectors.toMap(Enum::name, v -> v));

    private static final Map<String, Keyword> KEYWORDS =
            Arrays.stream(Keyword.values()).collect(Collectors.toMap(Enum::name, v -> v));

    private static final Set<Command> COMMANDS_WITH_KEYWORDS = Set.of(
            Command.ACL, Command.CLIENT, Command.CLUSTER, Command.CONFIG,
            Command.MEMORY, Command.MODULE, Command.OBJECT, Command.PUBSUB,
            Command.SCRIPT, Command.SLOWLOG, Command.XGROUP, Command.XINFO
    );

    public static @NotNull RedisQuery parseQuery(@Nullable String sql) throws SQLException {
        if (sql == null) throw new SQLException("Empty query.");
        String[] tokens = sql.split("\\s+");
        if (tokens.length == 0) throw new SQLException("Empty query.");
        Command redisCommand = parseCommand(tokens[0]);
        Keyword redisKeyword = parseKeyword(redisCommand, tokens.length > 1 ? tokens[1] : null);
        String[] params = Arrays.stream(tokens).skip(1).toArray(String[]::new);
        return new RedisQuery(redisCommand, redisKeyword, params);
    }

    private static @NotNull Command parseCommand(@NotNull String command) throws SQLException {
        Command redisCommand = COMMANDS.get(command.toUpperCase(Locale.ENGLISH));
        if (redisCommand == null)
            throw new SQLException(String.format("Query contains an unknown command: %s.", command));
        return redisCommand;
    }

    private static @Nullable Keyword parseKeyword(@NotNull Command redisCommand, @Nullable String keyword) throws SQLException {
        if (!COMMANDS_WITH_KEYWORDS.contains(redisCommand)) return null;
        if (keyword == null)
            throw new SQLException(String.format("Query does not contain a keyword for the command %s", redisCommand.toString()));
        Keyword redisKeyword = KEYWORDS.get(keyword.toUpperCase(Locale.ENGLISH));
        if (redisKeyword == null)
            throw new SQLException(String.format(
                    "Query contains an unknown keyword for the command %s: %s",
                    redisCommand.toString(),
                    keyword
            ));
        return redisKeyword;
    }
}
