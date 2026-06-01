package br.com.fiap.orbitcast.connection;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatabaseInitializer {

    @Inject
    DatabaseConnection databaseConnection;

    @ConfigProperty(name = "orbitcast.database.initialize", defaultValue = "true")
    boolean initializeDatabase;

    @ConfigProperty(name = "orbitcast.database.schema", defaultValue = "db/schema.sql")
    String schemaScriptPath;

    @ConfigProperty(name = "orbitcast.database.data", defaultValue = "db/data.sql")
    String dataScriptPath;

    void onStart(@Observes StartupEvent event) {
        if (!initializeDatabase) {
            return;
        }

        executeSql(schemaScriptPath);
        executeSql(dataScriptPath);
    }

    private void executeSql(String resourcePath) {
        String sql = loadScript(resourcePath);

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            for (String command : sql.split(";")) {
                String sanitized = command.strip();
                if (!sanitized.isEmpty()) {
                    statement.execute(sanitized);
                }
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao executar script " + resourcePath, exception);
        }
    }

    private String loadScript(String resourcePath) {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException("Recurso nao encontrado: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.strip().startsWith("--"))
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao ler recurso " + resourcePath, exception);
        }
    }
}
