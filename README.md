# Builder

Eliminate Builder boilerplate for Java classes

## Usage

Annotate your class with `@Builder`

```java
@Builder
public class User {
    String username;
    String mail;
    int age;
}
```

will generate:

```java
public final class UserBuilder {
  private String username;

  private String mail;

  private int age;

  public UserBuilder username(String username) {
    this.username = username;
    return this;
  }

  public UserBuilder mail(String mail) {
    this.mail = mail;
    return this;
  }

  public UserBuilder age(int age) {
    this.age = age;
    return this;
  }

  public User build() {
    User user = new User();
    user.username = this.username;
    user.mail = this.mail;
    user.age = this.age;
    return user;
  }
}
```

## License

Copyright 2016 Florian Wengelewski

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
