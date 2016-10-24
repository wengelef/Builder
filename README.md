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
