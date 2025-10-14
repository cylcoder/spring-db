package com.example.demo.connection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConst {

  public static final String URL = "jdbc:h2:tcp://localhost/~/Development/h2/databases/spring_db1";
  public static final String USERNAME = "sa";
  public static final String PASSWORD = "";

}
