package io.digdag.core.agent;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import io.digdag.spi.TemplateEngine;
import io.digdag.spi.OperatorFactory;
import io.digdag.spi.CommandLogger;

public class AgentModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(OperatorRegistry.class).in(Scopes.SINGLETON);
        binder.bind(AgentId.class).toProvider(AgentIdProvider.class).in(Scopes.SINGLETON);

        binder.bind(ConfigEvalEngine.class).in(Scopes.SINGLETON);
        binder.bind(TemplateEngine.class).to(ConfigEvalEngine.class).in(Scopes.SINGLETON);

        // log
        binder.bind(CommandLogger.class).to(TaskContextCommandLogger.class).in(Scopes.SINGLETON);
    }
}
