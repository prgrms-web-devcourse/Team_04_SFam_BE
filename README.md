# Team_04_Temp_BE

## 🤲🏻 프로젝트 소개

<div align="center"><img src="https://user-images.githubusercontent.com/93169519/184494576-9c85b5b1-c24a-4c89-956d-3babffa724b5.png" width="400"></div>
<br>

### Sports Family : SFAM

동네에서 같이 운동할 사람을 모아 팀을 만들고, 근처의 다른 팀들과 스포츠 경기를 진행할 수 있는 커뮤니티 서비스입니다.

## 🏠 서비스 주소

> https://www.dongkyurami.link/

## 🛠 기술 스택

<img src="https://img.shields.io/badge/Java 17-007396.svg?style=flat&logo=Java&logoColor=white"> <img src="https://img.shields.io/badge/Gradle 7.4.2-02303A.svg?style=flat&logo=Gradle&logoColor=white">
<img src="https://img.shields.io/badge/Spring Boot 2.7.0-6DB33F.svg?style=flat&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Security-6DB33F.svg?style=flat&logo=Spring Security&logoColor=white">
<img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=flat&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/JPA-AAAAAA.svg?style=flat&logo=Hibernate&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162.svg?style=flat&logo=JUnit5&logoColor=white">
<img src="https://img.shields.io/badge/Swagger-85EA2D.svg?style=flat&logo=Swagger&logoColor=white">
<img src="https://img.shields.io/badge/Flyway-CC0200.svg?style=flat&logo=Flyway&logoColor=white">
<img src="https://img.shields.io/badge/AWS-FF9E0F.svg?style=flat&logo=Amazon&logoColor=white">

## 📝 프로젝트 구조

![architecture](https://user-images.githubusercontent.com/93169519/184494566-1fa02ac2-995e-413c-b901-20f0f6d8303e.png)

## 🗺 ERD

![erd](https://user-images.githubusercontent.com/93169519/184494573-f2d54f6d-8e32-498e-aecd-b214ab28907d.png)

## 🌳 환경 설정

### Husky

git hook을 프로젝트 내에서 공유할 수 있도록 합니다.

**node가 없다면**

```
brew install node
```

**설치**

```
npm install
```

### Flyway

flyway.conf

```bash
flyway.url=[DB URL]
flyway.schemas=[DB SCHEMA]
flyway.user=[DB 사용자]
flyway.password=[DB 비밀번호]
flyway.locations=[Flyway 파일 위치]
```

### .env

```bash
MYSQL_USERNAME=
MYSQL_PASSWORD=
ENCRYPTOR_KEY=
```

## ⚙️ API 설계

### Swagger

API를 테스트 할 수 있는 UI를 제공합니다.

> https://api.dongkyurami.link/swagger-ui/index.html

## 👬 팀 소개

### [Backend]

<table>
  <tr>
    <td>
        <a href="https://github.com/HyoungUkJJang">
            <img src="https://avatars.githubusercontent.com/u/50834204?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/HYEBPARK">
            <img src="https://avatars.githubusercontent.com/u/35947674?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/midasWorld">
            <img src="https://avatars.githubusercontent.com/u/93169519?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/whyWhale">
            <img src="https://avatars.githubusercontent.com/u/67587446?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/pjh612">
            <img src="https://avatars.githubusercontent.com/u/62292492?v=4" width="100px" />
        </a>
    </td>

  </tr>
  <tr>
    <td><b>Crong (김형욱)</b></td>
    <td><b>Hyeb (박혜빈)</b></td>
    <td><b>Mark (곽동운)</b></td>
    <td><b>Elizabeth (김병연)</b></td>
    <td><b>NULL (박진형)</b></td>
  </tr>
  <tr>
    <td><b>Product Owner</b></td>
    <td><b>Scrum Master</b></td>
    <td><b>Developer</b></td>
    <td><b>Developer</b></td>
    <td><b>Developer</b></td>
  </tr>
</table>
<br/>

### [Frontend]

<table>
  <tr>
    <td>
        <a href="https://github.com/chmini">
            <img src="https://avatars.githubusercontent.com/u/39076382?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/kyubumjang">
            <img src="https://avatars.githubusercontent.com/u/33307948?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/dustmddus">
            <img src="https://avatars.githubusercontent.com/u/82739503?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/qq8721443">
            <img src="https://avatars.githubusercontent.com/u/61747121?v=4" width="100px" />
        </a>
    </td>

  </tr>
  <tr>
    <td><b>Papa (김창민)</b></td>
    <td><b>Lawrence (장규범)</b></td>
    <td><b>Claire (신승연)</b></td>
    <td><b>Thompson (홍정기)</b></td>
  </tr>
  <tr>
    <td><b>Product Owner</b></td>
    <td><b>Scrum Master</b></td>
    <td><b>Developer</b></td>
    <td><b>Developer</b></td>
  </tr>
</table>
<br/>
