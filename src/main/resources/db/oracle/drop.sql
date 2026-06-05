BEGIN
    FOR tabela IN (
        SELECT table_name
        FROM user_tables
        WHERE table_name IN (
            'T_OC_PLANO_COBERTURA',
            'T_OC_SIMULACAO',
            'T_OC_CAMPANHA_REGIAO',
            'T_OC_CAMPANHA',
            'T_OC_REGIAO',
            'T_OC_CANAL',
            'T_OC_CLIENTE',
            'PLANOS_COBERTURA',
            'SIMULACOES',
            'CAMPANHA_REGIAO',
            'CAMPANHAS_TRANSMISSAO',
            'REGIOES',
            'CANAIS',
            'CLIENTES'
        )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP TABLE ' || tabela.table_name || ' CASCADE CONSTRAINTS PURGE';
    END LOOP;
END;
/
