/* Setting up PROD DB */
create table parking(
    PARKING_NUMBER int PRIMARY KEY,
    AVAILABLE boolean NOT NULL,
    TYPE varchar(10) NOT NULL
);
