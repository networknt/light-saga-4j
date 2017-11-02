DROP Table IF Exists message;
DROP Table IF Exists received_messages;
DROP Table IF Exists saga_instance;
DROP Table IF Exists saga_instance_participants;

CREATE TABLE message (
  ID VARCHAR(120) PRIMARY KEY,
  DESTINATION VARCHAR(1000) NOT NULL,
  HEADERS VARCHAR(1000) NOT NULL,
  PAYLOAD VARCHAR(1000) NOT NULL
);

CREATE TABLE received_messages (
  CONSUMER_ID VARCHAR(120),
  MESSAGE_ID VARCHAR(120),
  PRIMARY KEY(CONSUMER_ID, MESSAGE_ID)
);

CREATE TABLE saga_instance(
  saga_type VARCHAR(100) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  state_name VARCHAR(100) NOT NULL,
  last_request_id VARCHAR(100),
  saga_data_type VARCHAR(1000) NOT NULL,
  saga_data_json VARCHAR(1000) NOT NULL,
  PRIMARY KEY(saga_type, saga_id)
);


CREATE TABLE saga_instance_participants (
  saga_type VARCHAR(100) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  resource VARCHAR(100) NOT NULL,
  PRIMARY KEY(saga_type, saga_id, destination, resource)
);
