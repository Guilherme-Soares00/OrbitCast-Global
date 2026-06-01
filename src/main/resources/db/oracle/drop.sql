BEGIN
    FOR tabela IN (
        SELECT table_name
        FROM user_tables
        WHERE table_name IN (
            'PLANOS_COBERTURA',
            'SIMULACOES',
            'CAMPANHA_REGIAO',
            'CAMPANHAS_TRANSMISSAO',
            'REGIOES',
            'CANAIS',
            'CLIENTES'
        )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP TABLE "' || tabela.table_name || '" CASCADE CONSTRAINTS PURGE';
    END LOOP;
END;
/
