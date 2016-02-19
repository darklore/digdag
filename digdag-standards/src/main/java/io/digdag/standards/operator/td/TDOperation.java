package io.digdag.standards.operator.td;

import java.io.Closeable;
import io.digdag.client.config.Config;
import io.digdag.standards.operator.BaseOperator;
import com.treasuredata.client.TDClient;
import com.treasuredata.client.TDClientException;
import com.treasuredata.client.TDClientHttpConflictException;
import com.treasuredata.client.TDClientHttpNotFoundException;

public class TDOperation
        implements Closeable
{
    public static TDOperation fromConfig(Config config)
    {
        String database = config.get("database", String.class);

        TDClient client = TDClient.newBuilder()
            .setEndpoint(config.get("endpoint", String.class, "api.treasuredata.com"))
            .setUseSSL(config.get("use_ssl", boolean.class, true))
            .setApiKey(config.get("apikey", String.class))
            .build();

        return new TDOperation(client, database);
    }

    private final TDClient client;
    private final String database;

    public TDOperation(TDClient client, String database)
    {
        this.client = client;
        this.database = database;
    }

    public TDClient getClient()
    {
        return client;
    }

    public String getDatabase()
    {
        return database;
    }

    public void ensureTableCreated(String tableName)
        throws TDClientException
    {
        try {
            // TODO set include_v=false option
            client.createTable(database, tableName);
        }
        catch (TDClientHttpConflictException ex) {
            // ignore
        }
    }

    public static String escapeIdentHive(String ident)
    {
        // TODO escape symbols in ident
        return "`" + ident + "`";
    }

    public static String escapeIdentPresto(String ident)
    {
        // TODO escape symbols in ident
        return "\"" + ident + "\"";
    }

    public void ensureTableDeleted(String tableName)
        throws TDClientException
    {
        try {
            client.deleteTable(database, tableName);
        }
        catch (TDClientHttpNotFoundException ex) {
            // ignore
        }
    }

    @Override
    public void close()
    {
        client.close();
    }
}