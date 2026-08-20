create table TRANSACTIONS
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    step              INT                                                            NOT NULL,
    type              ENUM('CASH_IN', 'CASH_OUT', 'DEBIT', 'PAYMENT', 'TRANSFER')    NOT NULL,
    amount            DECIMAL(15,2)                                                  NOT NULL,
    nameOrig          VARCHAR(255)                                                   NOT NULL,
    oldbalanceOrg     DECIMAL(15, 2)                                                 NOT NULL,
    newbalanceOrig    DECIMAL(15, 2)                                                 NOT NULL,
    nameDest          VARCHAR(255)                                                   NOT NULL,
    oldbalanceDest    DECIMAL(15, 2)                                                 NOT NULL,
    newbalanceDest    DECIMAL(15, 2)                                                 NOT NULL,
    isFraud           BOOLEAN                                                        NOT NULL,
    isFlaggedFraud    BOOLEAN                                                        NOT NULL
);

-- step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud