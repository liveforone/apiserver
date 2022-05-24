<h1>게시판 api 서버</h1>
<pre>
뷰단은 제작하지 않는다. 즉 테스트코드와 일반 코드로만 이루어짐.
DB는 테스트만 할 것이고 뷰단을 띄우지 않기 때문에 ddl-auto:create로 설정
</pre>

<h3>로그인 및 요구사항</h3>
<pre>
<strong>jwt 즉 json web token 방식으로 로그인을 진행한다.</strong>
<hr>
- 이메일, 비밀번호, 사용자 이름, 닉네임을 입력받아서 사용자 정보를 생성한다.
- 이메일과 닉네임의 중복은 허용되지 않는다.
- 사용자는 여러 개의 권한 등급을 가질 수 있다.
- 비밀번호는 날 것 그대로 저장하지 않는다.
- 닉네임은 변경할 수 있다.
</pre>

<h3>role</h3>
<pre>
사용자를 나타내는 Member 엔티티와 권한 등급을 나타내는 Role 엔티티 간의 
브릿지 테이블을 MemberRole 엔티티로 정의하였습니다. 
한 명의 사용자는 여러 개의 권한을 가질 수 있고, 여러 개의 권한은 
여러 사용자가 가지고 있을 수 있습니다. 
이를 Member와 Role 간에 @ManyToMany로 설정하면 브릿지 테이블을 위한 
엔티티를 별도로 선언하지 않아도 나타낼 수도 있지만, 
사용자가 가진 권한에 대해 어떤 추가적인 데이터가 기록될지 모르기 때문에, 
이에 대한 유연성을 위해 @OneToMany로 직접 선언하여 명시하였습니다. 
Member가 저장될 때 MemberRole 또한 연쇄적으로 저장되거나 제거될 수 있도록 
cascade 옵션을 ALL로, orphanRemoval=true로 설정해줍니다. 
Member가 제거되거나 연관 관계가 끊어져서 MemberRole이 고아 객체가 되었을 때, 
MemberRole은 Member와 생명 주기를 함께하며 제거될 것입니다. 
실제로 각 사용자가 가질 수 있는 권한 등급은, 그렇게 많지는 않겠지만, 
우리의 애플리케이션으로 조회 했을 때의 검색 성능 향상을 위해 Set으로 선언하였습니다.
</pre>

<h3>비밀번호 암호화와 토큰 발급 및 검증</h3>
<pre>
사용자는 입력한 정보로 회원가입을 하면, 이메일과 닉네임의 유효성(우리의 서비스 로직에서는 중복)을 검사하고,
변형된(암호화 또는 해시) 비밀번호로 저장해야합니다.
사용자는 로그인을 하면, 가입된 사용자인지 확인한 뒤에, 토큰을 발급받게 됩니다.
토큰은 액세스 토큰과 리프레쉬 토큰 두 가지를 발급합니다.
<u>액세스 토큰은, 사용자 인증에 사용될 것이기 때문에 유효 기간이 짧고,</u>
<u>리프레쉬 토큰은, 액세스 토큰을 재발급하는 용도로 사용될 것이기 때문에 유효 기간이 길도록 하겠습니다.</u>
</pre>

<h3>DTO 에서 사용하는 @Data</h3>
<pre>
@Data는 Getter, Setter, EqualsAndHashCode, ToString 등을 만들어줍니다.
</pre>

<h3>application-secret.yml</h3>
<pre>
지금은 access, refresh 토큰이 들어있는
application-secret.yml을 .gitignore에
넣지 않았지만 일반적인 경우라는 넣어주어야한다.
</pre>

<h3>TokenServiceTest</h3>
<pre>
<center><strong>&lt;디펜던시&gt;</strong></center>
우리는 TokenService만 테스트할 것이고, JwtHandler는 테스트하는데 필요한 행위 또는 상태만 제공해주면 됩니다.
이를 위해 사용할 수 있는 것이 Mockito 프레임워크입니다.
@InjectMocks를 필드에 선언하면, 의존성을 가지고 있는 객체들을 가짜로 만들어서 주입받을 수 있도록 합니다.
@Mock를 필드에 선언하면, 객체들을 가짜로 만들어서 @InjectMocks로 지정된 객체에 주입해줍니다.
위 예시에서는, TokenService에 필요한 JwtHandler를 가짜로 만들어서 주입해줍니다.
Mockito에서 제공해주는 given() 메소드를 이용하면, 의존하는 가짜 객체의 행위가 반환해야할 데이터를 미리 준비하여 주입해줄 수도 있고,
verify() 메소드를 이용하면, 그 가짜 객체가 수행한 행위도 검증할 수 있습니다.
<hr>
<center><strong>&lt;@BeforeEach 코드&gt;</strong></center>
TokenService는 @Value를 이용하여 설정 파일에서 값을 읽어와야합니다.
하지만 우리는 TokenService에 대해서 단위 테스트만 수행할 것이기 때문에, 해당 값을 읽어올 수 없습니다.
ReflectionTestUtils를 이용합니다.
이를 이용하면, setter 메소드를 사용하지 않고도 리플렉션을 이용해서 
어떠한 객체의 필드 값을 임의의 값으로 주입해줄 수 있게 됩니다.
<hr>
<center><strong>&lt;createAccessTokenTest&gt;</strong></center>
액세스 토큰을 생성하는 테스트 코드입니다.
Mockito에서 제공해주는 given 스태틱 메소드의 인자로, TokenService가 의존하고 있는 가짜 객체의 행위를 지정해주고,
이에 대한 반환 값의 메소드로, 이 객체의 행위가 반환해야 할 데이터를 준비해서 지정해줍니다.
이렇게 되면, TokenService 내의 로직을 수행하는 와중에,
given에 인자로 주어진 행위를 수행한다면, 준비된(willReturn) 데이터를 사용하게 됩니다.
TokenService가 의존하고 있는 JwtHandler도 테스트하게 되는 것이 아니라, TokenService의 코드에 대해서만 테스트를 할 수 있게 된 것입니다.
이에 대한 결과 값을 검증뿐만 아니라, Mockito의 verify 스태틱 메소드를 이용하면 행위 또한 검증할 수 있습니다.
verify의 인자로 어떤 행위가 수행되었는지 확인할 객체를 넘겨주고, 이에 대한 반환 값으로 확인할 메소드를 호출해주면 됩니다.
의존하고 있는 가짜 객체가 사용할 인자를 지정해줄 수도 있습니다.
코드에서 사용된 anyString(), anyLong(), any~() 등을 사용하면, 해당 자료형에 알맞은 어떤 인자가 사용되든 상관 없게 됩니다.
<hr>
<center><strong>&lt;createRefreshTokenTest&gt;</strong></center>
createAccessTokenTest와 동일
</pre>

<h3>생성 요청에 대한 결과 데이터를 꼭 응답해줘야하는가?</h3>
<pre>
생성 요청에 의해 어떤 데이터를 서버에 만들어내면, 
그거에 대한 결과 데이터까지 꼭 응답해줘야하는가?
<hr>
예를 들어, 사용자가 회원 가입을 성공적으로 끝마치면, 
회원가입한 사용자에 대한 정보를 응답으로 보내줘야하는가에 대한 문제입니다.
정상적인 사용자라면, 자신이 보낸 요청이 어떤 내용인지 알고 있습니다.
어떤 데이터를 보냈는지, 어떻게 처리될 것인지를 기대하고 있습니다.
이러한 까닭에, 굳이 알고 있는 정보에 대해서 다시 응답으로 보내줄 필요는 없다고 생각되었습니다.
자신이 가입 요청한 데이터가 무엇인지 알고 있고, 알고 있는 사실을 기반으로 로그인을 요청할 것이기 때문입니다.
<strong><u>네트워크 통신 비용은 비싼데, 이를 낭비할 이유는 없습니다.</u></strong>
<hr>
하지만 예외 사항도 있습니다.
어떤 글을 작성했다면, 보통 작성과 동시에 자신의 글 상세 조회 페이지로 이동하게 됩니다.
글을 조회하려면, 적어도 서버에서 생성된 글의 id 정도는 알아야하기 때문에, 
이런 경우에는 글의 id 값이나 리다이렉트할 URL 정도는 알려주어야합니다.
</pre>

<h3>로그인시 인증 및 인가</h3>
<pre>
- 클라이언트가 API를 요청한다. 이 때, 로그인해서 발급받은 액세스 토큰을 HTTP Authorization 헤더에 담아서 보내준다.
- 필터를 거친다. 우리가 작성한 JwtAuthenticationFilter에 도착한다. 
- 필터에서는 Authorization 헤더에서 토큰을 검증하고, 토큰으로 요청한 사용자 정보를 데이터베이스에서 조회해서 SecurityContext에 저장한다.
- 요청한 URL에 따라서 접근 허용 여부를 검사한다. 
- 인증되지 않은 사용자라면, 401 응답을 보내준다. (실제로는 401 응답을 내려주는 곳으로 리다이렉트)
- 요청한 자원에 접근 권한이 없다면, 403 응답을 보내준다. (실제로는 403 응답을 내려주는 곳으로 리다이렉트)
<hr>
사용자 요청 -> 필터 -> 
JwtAuthenticationFilter 토큰을 통해 컨텍스트에 사용자 정보 등록 -> <- CustomUserDetailsService, TokenService
요청과 접근 정책에 따른 검사 -> <- Guard -> <- AuthHelper
검증실패시 CustomAuthenticationEntryPoint, CustomAccessDeniedHandler 작동 후 /exception으로 리다이렉트 -> Exception Controller로 요청, ExceptionAdvice 동작하여 응답
컨트롤러 요청 도달
</pre>

