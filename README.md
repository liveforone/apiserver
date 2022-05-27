<h1><mark>&lt;게시판 api 서버&gt;</mark></h1>
<pre>
뷰단은 제작하지 않는다. 즉 테스트코드와 일반 코드로만 이루어짐.
DB는 테스트만 할 것이고 뷰단을 띄우지 않기 때문에 ddl-auto:create로 설정
</pre>

<h2><mark>&lt;post man 으로 api 요청&gt;</mark></h2>
<pre>
- 회원가입- 
/api/sign-up
body ->
"email" : "email@email.com",
"password" : "123456a!",
"username" : "username",
"nickname" : "nickname"
<hr>
- 로그인 -
/api/sign-in
body -> 
"email" : "email@email.com",
"password" : "123456a!"
이때 accessToken과 RefreshToken이 발급됨
refreshToken을 복사하고 맨 앞의 Bearer
을 제외한 나머지 모두를(""제외) 복사한다.
<hr>
- accessToken 재발급 -
/api/refresh-token
Authorization -> type = Bearer Token
Token -> 위에 복사한 값 입력
어세스 토큰이 재발급 된다.
</pre>

<h3>로그인 요구사항</h3>
<pre>
<strong>jwt 즉 json web token 방식으로 로그인을 진행한다.</strong>
<hr>
- 이메일, 비밀번호, 사용자 이름, 닉네임을 입력받아서 사용자 정보를 생성한다.
- 이메일과 닉네임의 중복은 허용되지 않는다.
- 사용자는 여러 개의 권한 등급을 가질 수 있다.
- 비밀번호는 날 것 그대로 저장하지 않는다.
- 닉네임은 변경할 수 있다.
</pre>

<h3>게시판 기능 요구사항</h3>
<pre>
- 계층형 카테고리
- 물품 판매 게시글 CRUD
- 게시글 조건 검색
- 물품 주문 요청
- 계층형 대댓글
- 매매 내역 조회
- 게시글 별 쪽지 송수신
- 페이지 번호를 이용한 페이징 처리(게시글 조회)
- 무한 스크롤을 이용한 페이징 처리(쪽지 및 매매 내역 조회)
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

<h3>엔티티와 DTO가 테스트코드에서 중복됨</h3>
<pre>
각 테스트(테스트 클래스에 작성되는 메소드)마다 작성되는 인스턴스 생성 코드의 중복을 제거하기 위해, 
테스트 클래스의 메소드로 추출하여 엔티티 또는 DTO 인스턴스를 생성하고 있습니다.
이렇게 해서 하나의 테스트 클래스 내에 작성되는 인스턴스 생성 코드의 중복은 제거할 수 있었지만, 
여러 테스트 클래스에서 걸쳐서 동일한 엔티티 또는 DTO 클래스를 생성하는 코드는 계속해서 중복되고 있습니다.
지금은 테스트가 몇 개 작성되지 않았기 때문에 많은 수의 중복은 나타나고 있지 않지만, 
테스트가 늘어남에 따라 동일한 인스턴스 생성 코드의 중복이 지속적으로 증가할 우려가 있습니다.
중복을 제거하기 위해, 엔티티와 DTO 인스턴스 생성을 담당해주는 팩토리 클래스를 만들겠습니다.
</pre>

<h3>액세스 토큰을 재발급할 때, 리프레시 토큰도 재발급해주지 않은 이유</h3>
<pre>
각 사용자에게 발급된 리프레시 토큰에 대해서 데이터베이스에 별도로 저장을 해주었기 때문에, 
리프레시 토큰이 탈취당하더라도 사용자는 새롭게 로그인하면 기존의 리프레시 토큰을 무효화시킬 수 있었습니다.
하지만 무상태성을 위해 채택한 토큰 인증 방식의 장점을 굳이 해치지 않았고 싶었기 때문에 리프레시 토큰으로 관리해주지않았습니다.
이로 인해 리프레시 토큰이 탈취당할 경우, 해당 토큰을 이용해서 액세스 토큰을 무한정으로 재발급할 수 있게 됩니다.
이에 대한 최소한의 안전장치를 마련하고자, 
로그인 할 때 발급 받은 리프레시 토큰은, 해당 토큰이 만료된다면 더 이상 유효하지 않도록 만든 것입니다.
</pre>

<h3>TokenService 코드 중복 리팩토링</h3>
<pre>
SignService는 각각의 토큰 종류마다 다른 설정 정보를 가지는, 
동일한 타입의 인스턴스를 의존하면 문제가 해결되는 것입니다.
SignService에서 의존할 동일한 타입의 인스턴스를 스프링 컨테이너에서 주입받는다.
DI를 이용하여 의존성을 만드는 것은 스프링의 철학을 유지할 수도 있고, 
SignService가 토큰 설정 정보를 다루는 방식에 대해서 직접적으로 몰라도 됩니다. 
주입 받을 인스턴스에서 처리해두면 됩니다.
그렇다면, 동일한 타입의 인스턴스를 상태만 달리하여 스프링 컨테이너에 등록시키면 문제가 해결됩니다.
이를 수행하기 위해, @Bean 어노테이션으로 직접적으로 스프링 빈에 등록하는 방식을 택하게 되었습니다.
</pre>

<h3>SwaggerConfig</h3>
<pre>
SwaggerConfig에서 @Import(BeanValidatorPluginsConfiguration.class)를 해주었습니다.
이를 이용하면 @Valid가 선언된 DTO 객체들의 bean validation 조건을 문서화할 수 있습니다.
</pre>

<h3>swagger 사용</h3>
<pre>
/swagger-ui/index.html
로 접속하면 된다. postman과 비슷
잘모를땐
https://kukekyakya.tistory.com/540?category=1025994
링크 참조
</pre>

<h3>DB 계층</h3>
<pre>
만약 어떤 카테고리를 생성한 뒤에, 그에 대한 하위 카테고리를 생성하려면, 그 상위 카테고리는 이미 데이터베이스에 저장되어 있을 것입니다.
하위 카테고리는 상위 카테고리보다 먼저 생성될 수 없습니다.
우리는, 상위 카테고리를 지정하여 하위 카테고리를 생성할 수 있습니다.
우리가 지정한 id 생성 전략에 의해서, 카테고리의 id는 순차적으로 증가합니다.
즉 id는 데이터가 생성된 순서를 의미하기 때문에, 두번째 정렬 조건으로 id 오름차순을 지정해준다면,
같은 부모 카테고리를 가지는 하위 카테고리들은, 생성된 순서로 정렬될 수 있습니다.
우리는 부모 카테고리의 id 값을 첫번째 정렬 조건으로 사용하고 있습니다. 부모 카테고리 id 값은 외래 키로 지정되어있고, 
외래 키에 대한 인덱스 자동 생성 여부는 데이터베이스 종류에 따라 다르지만, 
카테고리의 개수는 그리 많지 않을 것이기 때문에 인덱스가 없더라도 큰 지장이 없을 것입니다. 
만약 인덱스 생성이 되어있지 않다면, 상황에 따라 외래 키에도 인덱스를 직접 생성해주면 될 것입니다.
</pre>

<h3>@DataJpaTest 에러</h3>
<pre>
@DataJpaTest를 사용하면 자동으로 EmbeddedDatabase를 사용해버리기 때문에, mysql을 설정을 해버릴 수가 없다.
방법은 바로 AutoConfigureTestDatabase를 덮어써서 해당 설정이 동작하지 않게 바꿔버리면 된다.
<mark><u>@AutoConfigureTestDatabase(replace = Replace.NONE)</u></mark>
</pre>

<h3>get(0) 에러</h3>
<pre>
get(0)으로 넘길때
java.lang.indexoutofboundsexception: index 0 out of bounds for length 0
위의 에러가 발생한다.
이때는 if문으로 해당 객체의 .size() 가 0이 아닐경우에 동작하도록
바꾸어주면 된다.
</pre>

<h3>카테고리 삭제시 select 쿼리 두번나감</h3>
<pre>
두 개의 SELECT 쿼리의 WHERE 문 조건을 보아하니, 유사한 동작을 수행하고 있습니다.
단지, 첫번째 쿼리는 count(*)로 조회하고, 두번째 쿼리는 컬럼 전체를 조회하는 것이었습니다.
CategoryRepository.exsitsById를 호출하여 카테고리가 있는지 확인한 뒤에,
CategoryRepository.deleteById를 호출하여 해당 카테고리 id 값을 통해 카테고리 삭제를 수행하는 코드입니다.
SELECT count(*) 쿼리는 existsById에 의해서 호출되는 쿼리일 것입니다.
이 쿼리로 인해 카테고리가 있는지 확인했으니,
그 다음으로는 분명 deleteById에 의해서 단일한 DELETE FROM WHERE category_id = ? 쿼리가 하나 전송될 것이라고 여겼습니다.
하지만 그 사이에서 또 다른 SELECT 쿼리가 일어나고 있던 것입니다.
deleteById는 내부적으로 findById를 수행한 뒤, delete에 조회된 결과를 인자로 넘겨주고 있었습니다.
단일한 DELETE 쿼리 하나만 생성될 것이라는 기대와는 달리, findById를 이용한 조회 작업이 함께 수행되고 있던 것입니다.
삭제해야할 데이터가 없는 경우 EmptyResultDataAccessException 예외가 발생하는 것은 알았지만, 
데이터베이스에서 DELETE 쿼리의 결과로 해당 예외가 발생하는 줄 알았던 것이지, 
그 전에 SELECT 쿼리로 미리 확인하는 줄은 몰랐던 것입니다.
findById는 모든 컬럼도 같이 조회하기에, exists로 존재 여부만 가볍게 확인하고자 하였는데, 
오히려 의미없는 하나의 쿼리를 더 생성하고 있었습니다.
따라서 MemberServiceTest와 CategoryServiceTest를 수정했습니다.
</pre>