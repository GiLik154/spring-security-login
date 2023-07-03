# spring-security-login

<details>
<summary>Spring Security의 로그인 처리</summary>

## 0. 개요

Spring Security로 로그인 기능을 구현하면 많은 이점이 있다.

1. 세션의 처리를 자동으로 해주고, 그 세션을 통해 유저의 정보를 파싱할 수 있다.
2. 보안적인 처리를  스프링 시큐리티에 의존하여 편하게 작업할 수 있다.
3. 권한 저리 처리를 스프링 시큐리티에 의존하여 편하게 작업할 수 있다. 

이 외에도 계정 만료 처리, 비밀번호 만료 처리 등 많은 처리를 손쉽게 할 수 있도록 도와주지만, 셋팅이 조금 복잡하고 어려운 것들이 많다.
그래서 그러한 것들을 하나씩 정리해보려고 한다.

## 1. Spring Security의 Login Config 셋팅

```java
public class SecurityConfig {
    /**
     * Spring Security에 대한 기본적인 설정을 정의하는 메서드입니다.
     * 이 메서드는 SecurityFilterChain을 생성하여 반환하는 Bean으로 등록됩니다.
     *
     * @param http HttpSecurity 객체, Spring Security 설정을 구성하기 위한 메서드가 제공됩니다.
     * @return SecurityFilterChain 객체, Spring Security 필터 체인을 나타냅니다.
     * @throws Exception HttpSecurity 구성 중에 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                    .csrf()
                .and()
                    .authorizeRequests()
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/**").permitAll()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                .and()
                    .build();

        // `csrf()`: CSRF(Cross-Site Request Forgery)에 대한 설정을 활성화합니다.
        // `authorizeRequests()`: 요청에 대한 접근 권한 설정을 시작합니다.
        // `antMatchers("/admin").hasRole("ADMIN")`: '/admin' 경로에 접근하기 위해 'ADMIN' 권한이 필요합니다.
        // `antMatchers("/**").permitAll()`: 모든 경로에 대한 접근을 허용합니다.
        // `formLogin()`: 폼 로그인에 관한 설정을 시작합니다.
        // 'loginPage("/login")' 로그인 페이지를 설정합니다 (Get 요청)
        // `loginProcessingUrl("/login")`: 로그인 처리를 담당하는 URL을 설정합니다. (Post 요청)
        // `defaultSuccessUrl("/")`: 로그인 성공 후 사용자를 리디렉션할 기본 URL을 설정합니다.
        // `logout()`: 로그아웃에 관한 설정을 시작합니다.
        // `logoutUrl("/logout")`: 로그아웃 처리를 담당하는 URL을 설정합니다.
        // `logoutSuccessUrl("/login")`: 로그아웃 성공 후 사용자를 리디렉션할 URL을 설정합니다.
        // `build()`: SecurityFilterChain을 빌드하여 반환합니다.
    }
```


위에 주석으로 다 달아두었지만 다시 설명을 한다.

예전에는 `WebSecurityConfigurerAdapter` 를 상속 받아서 오버라이딩으로 구현했으나, 현재는 빈으로 구현하고 있다.

![스크린샷 2023-06-01 오후 9 08 09](https://github.com/GiLik154/spring-security-login/assets/118507239/12347f31-d50b-4760-a3ec-b1b595870d57)

상세 정보에 들어가면 주석에 Bean으로 등록하는 방법을 친절하게 잘 적어뒀다.
쉽게 설명하면 and()가 하나의 괄호 라고 생각을 하면 편할 것 같다.
예시를 들어 

```java
								.and()
                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
```

`formLogin()` 의 처리가 끝나면 and로 괄호를 닫고 `.logout()` 로 새로 괄호를 열어 코딩을 하는 느낌이었다. 따라서 위와 같이 줄을 정리해놓은면 가독성이 더 좋을 것 같다.

나의 셋팅을 하나씩 살펴보면
CSRF을 사용하고
”/admin” 사이트에는 ADMIN이라는 등급을 가지고 있는 유저만 접근이 가능하며
그 외 사이트는 모든 사람이 접근이 가능한다.
로그인 사이트는 “/login” 을 사용하며, 성공하면 “/”로 이동한다.
로그아웃 사이트는 “/logout”을 사용하며 로그아웃에 성공하면 “/login”으로 이동한다.

위와 같은 셋팅을 지니고 있다.

이후에 셋팅을 해야 하는 부분을 UserDetails부분이다.

## 2. Spring Security의 UserDetails셋팅

![무제](https://github.com/GiLik154/spring-security-login/assets/118507239/be23fd51-940f-4f96-a7e1-baed17fda53c)

위와 같이 설정을 하면 된다.
각 메소드는 주석을 달아놓았다.

여기서 우리가 주의깊게 봐야하는 부분이 몇 군대 있는데

![스크린샷 2023-06-01 오후 9 21 21](https://github.com/GiLik154/spring-security-login/assets/118507239/3955e0fd-1ed1-48f5-9d14-ba19a6348ea3)

우선 User을 가지고 있어야만 생성될 수 있도록 생성자를 만드는 부분이 필수적이다.
이 User의 정보를 가지고 검증을 하기 때문이다.

![Untitled (2)](https://github.com/GiLik154/spring-security-login/assets/118507239/cda151a9-bbbb-46fa-af6c-2a6fa326d0bd)

이런식으로 Password와 username을 반환해주어야 인증을 할 수 있다.
위에는 유저의 등급을 확인하는 구간도 있는데, 그 쪽은 이따 등급에 관해서 이야기를 할 때 설명하도록 하겠다.
자 이쯤에서 나는 궁금한 것이 생겼다.
대체 어떻게 저 user을 가지고 오는거지…?
우선 

## 3. Spring Security의 UserDetailsService셋팅

![Untitled (3)](https://github.com/GiLik154/spring-security-login/assets/118507239/10518d51-4634-46e9-bab6-b5c6e7b38c88)

`UserDetailsService`를 구현하면 위와 같은 형식으로 작성하게 된다.
UserDetails를 상속받은 `UserDetailsImp`를 리턴해주어야 한다.
여기서 각 ORM에 따라 조금은 다른 방식으로 처리가 될 것이다.
꼭 User을 반환할 필요는 없지만, 우리는 객체 지향 언어인 자바를 하고 있기에, 객체를 반환해주는 쪽으로 작업을 했다.

아니 대체 어디서 거쳐서 여기로 오는건가.. 싶었다.

## 4. Spring Security의 로그인 처리 로직?

![Untitled (4)](https://github.com/GiLik154/spring-security-login/assets/118507239/58c69055-89f2-42db-9d0b-a488ea56f3ab)

위의 코드에서 `DaoAuthenticationProvider` 에서 오는거였다.
`AuthenticationProcessingFilter` ****에서 username과 password를 받아 

`UsernamePasswordAuthenticationToken` 토큰 객체를 만들고
`AuthenticationManager` 에 전달된다. 이후 순차적으로 인증 처리를 진행 한다.

`DaoAuthenticationProvider` 에서는 `UserDetailsService` 를 사용해서 사용자 정보를 가지고 오고, `UserDetailsService` 는 username을 통해 `UserDetails` 를 반환한다. 반환된 `UserDetails`를 통해 

`DaoAuthenticationProvider` 는 비밀번호를 비교하여 인증 후에 `Authentication` 를 반환하고, 
*(정확히는 `DaoAuthenticationProvider` 부모인 `AbstractUserDetailsAuthenticationProvider` 에서 반환되는 것 같다.)*
`SecurityContextHolder`에 전달된다. 

즉, 로그인의 대부분의 처리는 `DaoAuthenticationProvider` 에서 처리가 된다는 이야기이다. 

## 5. 주저리 주저리

공부하면서 머리 깨지는 줄 알았다. 보면서 스프링 시큐리티 코드가 아름답다고 생각이 들기도 했다. 언젠가는 저런 코드를 내가 작성할 수 있을까? 라는 생각도 많이 들었고, 생각보다 시큐리티 코드가 굉장히 광범위하고 넓었다.
시큐리티를 공부하면 정말 공부할 게 많다는 이야기를 들었는데, 실제로 공부해보니 그 양이 어마어마 했다.
심지어 왜? 작동하는지에 대해서 의문을 가지는 사람조차 많지 않아 정보를 찾는데에 어려움이 많이 있었다.
내가 조사한 자로들이 여러분들게 조금이나마 도움이 되었으면 좋겠고, 더 나은 문서를 작업하기 위해 노력해보도록 하겠습니다.

  
</details>
 
  
<details>
<summary>Spring Security의 권한 설정</summary>
  
  ## 0. 개요

스프링 시

Spring Security의 권한을 이용하면 다양한 것을 쉽게 사용이 가능하다.
예를 들면 Admin 사이트에 권한이 없으면 접근하지 못하는 경우 등이 그렇다.

## 1. Spring Security의 Login Config 셋팅

  ![Untitled (2)](https://github.com/GiLik154/spring-security-login/assets/118507239/f1decf60-67a6-46e3-8aee-99e0b560cc96)

  ![Untitled (3)](https://github.com/GiLik154/spring-security-login/assets/118507239/16442a14-99ba-4e41-962d-dc999c483e63)


자세한 설명은 로그인에 관해서 적어놓은 글에 적어두었습니다.
위의 링크는 테스트를 위하여 설정하였습니다.

## 2. Enum 셋팅

![Untitled (4)](https://github.com/GiLik154/spring-security-login/assets/118507239/180f6b4b-e78b-44af-be8d-1a79d915b946)

테스틀 위하여 3가지 단계를 설정하였습니다.
또한 Spring Security의 경우 ROLE_이라는 접두사가 붙어야 하기 때문에 Get메소에 
`return "ROLE_" + name();` 와 같이 설정하였습니다.

ROLE_을 생략하고 싶다면

![Untitled (5)](https://github.com/GiLik154/spring-security-login/assets/118507239/bd4f2b3c-fea6-49ee-be99-a9e368a2eaa9)

위와 같이 `hasAnyAuthority` 를 사용하면 되긴한다.
하지만 스프링 시큐리티의 기본 로직은 ROLE_ 을 사용하기 때문에 권장하지는 않는 방법인 듯 하다.

## 3. UserDetails 셋팅

![Untitled (6)](https://github.com/GiLik154/spring-security-login/assets/118507239/6b7b9ab3-3096-4297-91e2-11e80c9c6a91)

이후 위와 같은 방식으로 사용자의 등급을 넣어주어야 한다.

![Untitled (7)](https://github.com/GiLik154/spring-security-login/assets/118507239/650a060c-51ad-4512-865b-de97f6c06f0c)

로그인을 하고 디버깅을 찍어보면 아래와 같이 정보들이 잘 들어가는 것을 볼 수 있다.

## 4-0 init

![Untitled (8)](https://github.com/GiLik154/spring-security-login/assets/118507239/cea82e00-eb4e-45d5-a88a-27b36bfc381c)

2개의 계정을 준비했고

![Untitled (9)](https://github.com/GiLik154/spring-security-login/assets/118507239/ce6b795e-0649-4241-932f-9b80776e4e56)

2개의 사이트를 준비하였다.

## 4-1 USER등급의 사이트 접속

우선 유저로 접속을 하면
  
<img width="421" alt="Untitled (10)" src="https://github.com/GiLik154/spring-security-login/assets/118507239/6abd6dba-bf1b-4899-9361-b5f11ccd0b3c">


user 사이트에는 접속이 잘 되나, 

<img width="464" alt="Untitled (11)" src="https://github.com/GiLik154/spring-security-login/assets/118507239/fd62ace0-31de-4dec-b9f7-de0aeee7fefb">

admin 사이트에는 접속이 안되는 것을 볼 수 있다. 

## 4-2 ADMIN 등급의 사이트 접속

admin의 경우는 user사이트에도 접속이 되고

<img width="446" alt="Untitled (12)" src="https://github.com/GiLik154/spring-security-login/assets/118507239/5abceb46-99dc-48dd-9b00-b85b7b3485d0">

admin 사이트에도 접속이 잘 되는것을 볼 수 있다.

<img width="456" alt="Untitled (13)" src="https://github.com/GiLik154/spring-security-login/assets/118507239/0fdf37b5-31c6-4be1-8534-cd08cc5050b4">

## 5. 결론

위와 같이 스프링 시큐리티의 권한을 이용하면 쉽게 사이트 별 접속 권한을 설정해 줄 수 있고
손 쉽게 코딩을 진행할 수 있다.
원래라면 인터셉터나 필터를 통해 우리가 구현해주어야 하는 부분을 쉽게 구현할 수 있다는 장점이 있다.
단점으로는 구현하는 것이 조금 까다롭고, `UserDetails` 을 필수로 구현을 하다 보니 개개인의 코딩 스타일에 맞지 않는 경우도 있을 수 있다는 생각이 든다.
하지만 편리하고 강력한 기능인 것을 확실하니, 알고 있다면 도움이 많이 될 것이라고 생각했다.
  
  
  
  
  
</details>

<details>
<summary>Spring Security의 유저 정보 처리 방법</summary>
  ## 0. 개요

Spring Security의 로그인 기능을 이용하면 유저의 정보를 받아오는 것이 매우 편해진다.
`UserDetails`의 정보를 알아서 파싱해주기 때문이다.
어떤식으로 파싱이되고, 어떻게 하면 정보들을 읽어올 수 있는지에 대해서 알아보려고 한다.

## 1. Spring Security의 처리 방법?

Spring Security는 세션을 사용하는 방법으로 로그인 처리를 진행한다.

![Untitled (14)](https://github.com/GiLik154/spring-security-login/assets/118507239/81adbf9d-2bcc-453d-b18d-6edd9759d15f)

로그인을 시도하면 JSESSIONID를 이렇게 반환하는것을 알 수 있다.
이것이 세션 키라는 것을 대부분의 사람들은 다 알고 있을 것이다.

![Untitled (15)](https://github.com/GiLik154/spring-security-login/assets/118507239/1792cd11-5915-4ab7-95c8-b0e903c4e2b1)

세션을 뜯어보기 위해서 위와 같은 코드를 작성했다.

![Untitled (16)](https://github.com/GiLik154/spring-security-login/assets/118507239/53fd63fd-79a3-4d2c-b2ba-bdfbea5cde4b)

`SPRING_SECURITY_CONTEXT` 를 통해서 `SecurityContext` 를 받아올 수 있고,
`SecurityContext` 안에는 `Authentication` 가 있는 것을 확인할 수 있다.
`Authentication` 의 안에는 `getPrincipal`룰 통해 `userDetailsImpl` 를 받아올 수 있는 것을 확인할 수 있다.
`userDetailsImpl` 은 우리가 이전에도 본 것 처럼 유저의 정보가 담겨있다.

`userDetailsImpl.getUsername()` 를 하면 이제 로그인 한 유저의 이름을 받아올 수 있게 된다.

위의 로직을 위해 세션을 보내주는 곳은

![Untitled (17)](https://github.com/GiLik154/spring-security-login/assets/118507239/867f7fca-32eb-4e47-bb3e-f6e546d239cb)

이곳이었다. 이곳에서 로직을 살펴보니
  
![Untitled (18)](https://github.com/GiLik154/spring-security-login/assets/118507239/2df6bfc0-563d-4bd9-8dd8-a8e2fca93a59)

위와 같은 로직을 실행 후에 `httpSession.setAttribute(springSecurityContextKey, context);` 를 하는 것을 살펴볼 수 있었다.

하지만 이렇게 매번 세션을 파싱해서 하는 방법은 너무 귀찮고 복잡하고 세션의 name을 외워야 하는 불상사가 있다. 이것을 해결해주기 위해서 스프링 부트는 2가지 방법을 더 제시해주고 있다.

## 2. Spring Boot에서 권하는 방법은?

![Untitled (19)](https://github.com/GiLik154/spring-security-login/assets/118507239/7688bd6c-0e40-4f51-8172-855455ca9521)

우선 2가지를 전체적으로 보면 이 차이이다.

`SecurityContextHolder`를 통해 `Authentication` 를 받아오거나
`Authentication` 를 매개 변수로 받아올 수 있다.

이것은 취향에 따라 다르지만, 결국 똑같이 작동한다는 것을 알 수 있다.
어느 쪽을 선택 하는 것이 가독성이 더 좋은지에 따라는 사람마다 다를 것이다.

## 3. User의 다른 정보를 가지고 오고 싶은데?

이건 아주 쉽다. 내가 구현한 `UserDetails` 에 get메소드만 더 넣어주면 된다.

![Untitled (20)](https://github.com/GiLik154/spring-security-login/assets/118507239/de6f8a25-680d-43f1-82aa-f21d1f352b93)

위와 같은 방식으로 말이다.
단 주의해야 할 점은, 보안적인 요소를 신경써서 어디까지 우리가 의존해도 될까? 를 고민하면 될 것 같다.

## 4. 결론

Spring Security에서 로그인을 하고 유저 정보를 읽어오는 로직이 궁금했다.
그래서 많이 찾아봤지만, 세션을 이용한다는 것을 빼고 어떤식으로 작동하는지에 대한 설명은 많지 않았다.
세션을 어디서 보내고, 어떤 이름으로 보내고 어떤 로직들이 처리가 되어야 하는지에 대한 내용이 많지 않아
조사하는데 시간이 조금 많이 걸렸다.
하지만 이렇게 조사하고 나니 조금 속이 시원하긴 하지만, 정말 복잡하게 얽혀있는 로직이라는 생각이 들었다.
이것을 알고 있다고 하여서 도움이 많이 되진 않겠지만, 그래도 나 처럼 궁금한 사람들에게는 도움이 되었으면 좋겠다.

</details>
