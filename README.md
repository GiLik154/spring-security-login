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

![Untitled (1)](https://github.com/GiLik154/spring-security-login/assets/118507239/bfc3792c-8d7c-48da-91cb-40fa38a633a4)


위에 주석으로 다 달아두었지만 다시 설명을 한다.

예전에는 `WebSecurityConfigurerAdapter` 를 상속 받아서 오버라이딩으로 구현했으나, 현재는 빈으로 구현하고 있다.

![스크린샷 2023-06-01 오후 9 08 09](https://github.com/GiLik154/spring-security-login/assets/118507239/12347f31-d50b-4760-a3ec-b1b595870d57)

상세 정보에 들어가면 주석에 Bean으로 등록하는 방법을 친절하게 잘 적어뒀다.
쉽게 설명하면 and()가 하나의 괄호 라고 생각을 하면 편할 것 같다.
예시를 들어 

![스크린샷 2023-06-01 오후 9 09 32](https://github.com/GiLik154/spring-security-login/assets/118507239/351defcd-e36d-42ee-bb3a-277bd3012552)

`formLogin()` 의 처리가 끝나면 and로 괄호를 닫고 `.logout()` 로 새로 괄호를 열어 코딩을 하는 느낌이었다. 따라소 위와 같이 줄을 정리해놓은면 가독성이 더 좋을 것 같다.

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
