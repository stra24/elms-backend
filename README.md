# eラーニング管理システム（ELMS） — バックエンド

受講生がオンラインでコース・レッスンを学習し、管理者がコース・ユーザー・お知らせを管理できる eラーニングプラットフォームのバックエンド API サーバーです。

## 技術スタック

| 分類 | 技術 |
|---|---|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 3.5.8 |
| DB アクセス | Spring Data JDBC（JPA は不使用） |
| DB | PostgreSQL 17 |
| マイグレーション | Flyway |
| 認証 | JWT（HttpOnly Cookie）+ RefreshToken |
| パスワード | BCrypt |
| メール送信 | Spring Mail（開発環境は Mailpit） |
| ファイルストレージ | ローカルディスク（開発）/ AWS S3（本番） |
| API ドキュメント | Springdoc OpenAPI (Swagger UI) |
| コードフォーマット | Spotless / Google Java Format |
| テスト | JUnit 5 + Testcontainers（PostgreSQL） |
| アーキテクチャ | オニオンアーキテクチャ + DDD（Presentation / Application / Domain / Infrastructure） |

## 機能一覧

**一般ユーザー（GENERAL ロール）**
- コース一覧・詳細閲覧
- レッスン視聴・受講完了マーク
- お知らせ一覧・詳細閲覧
- アカウント情報更新・パスワード変更
- パスワードリセット（メール認証）

**管理者（ADMIN ロール）**
- コース CRUD・サムネイル設定
- レッスングループ CRUD・並び順変更
- レッスン CRUD・並び順変更
- ユーザー CRUD・CSV インポート / エクスポート
- お知らせ CRUD
- ファイルアップロード（画像）

## 前提条件

- Java 21
- Docker / Docker Compose

## セットアップ

### 1. 環境変数ファイルを作成

```bash
cp .env.dev.example .env
```

`.env` の主な設定項目：

```env
DB_HOST=localhost
DB_NAME=elms_db
DB_USER=root
DB_PASS=pass
DB_PORT=5433
MAIL_HOST=localhost
MAIL_PORT=1025
BASE_URL=http://localhost:3000
JWT_SECRET=<base64エンコードの32バイト以上のシークレット>
COOKIE_SECURE=false
UPLOAD_DIR=uploads
```

JWT シークレットの生成例：
```bash
openssl rand -base64 32
```

### 2. ミドルウェアを起動

```bash
docker compose -f docker-compose-dev.yml up -d
```

| コンテナ | 用途 | ポート |
|---|---|---|
| elms-db | PostgreSQL 17 | 5433 |
| elms-mailpit | メール確認（Mailpit） | SMTP: 1025 / UI: 8025 |

### 3. アプリケーションを起動

```bash
./gradlew bootRun
```

初回起動時に Flyway が自動でスキーマ作成とシードデータ投入を行います。

サーバーは **http://localhost:8080** で起動します。

## シードデータ（初期アカウント）

| メールアドレス | パスワード | ロール |
|---|---|---|
| kanri@test.com | password | ADMIN |
| ippan@test.com | password | GENERAL |

## API ドキュメント

起動後、以下の URL で Swagger UI を確認できます。

```
http://localhost:8080/swagger-ui/index.html
```

## 主要エンドポイント一覧

### 認証
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| POST | /api/login | ログイン（JWT + RefreshToken を Cookie にセット） | 不要 |
| POST | /api/logout | ログアウト（Cookie を削除） | 不要 |
| GET | /api/auth/refresh | アクセストークン更新 | 不要 |

### パスワードリセット
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| POST | /api/password-reset/request | リセットメール送信 | 不要 |
| POST | /api/password-reset/confirm | トークン検証・パスワード更新 | 不要 |

### コース
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/courses | コース一覧 | 要認証 |
| GET | /api/courses/{courseId} | コース詳細 | 要認証 |
| POST | /api/courses | コース作成 | ADMIN |
| PUT | /api/courses/{courseId} | コース更新 | ADMIN |
| DELETE | /api/courses/{courseId} | コース削除 | ADMIN |

### レッスングループ
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| POST | /api/courses/{courseId}/lesson-groups | レッスングループ作成 | ADMIN |
| PUT | /api/courses/{courseId}/lesson-groups/{lessonGroupId} | レッスングループ更新 | ADMIN |
| DELETE | /api/courses/{courseId}/lesson-groups/{lessonGroupId} | レッスングループ削除 | ADMIN |

### レッスン
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/courses/{courseId}/lessons | コース内レッスン一覧 | 要認証 |
| GET | /api/courses/{courseId}/lessons/first | 最初のレッスン取得 | 要認証 |
| POST | /api/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons | レッスン作成 | ADMIN |
| GET | /api/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId} | レッスン詳細（管理者用） | ADMIN |
| PUT | /api/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId} | レッスン更新 | ADMIN |
| DELETE | /api/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId} | レッスン削除 | ADMIN |
| PUT | /api/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId}/order | レッスン並び順変更 | ADMIN |

### ユーザー受講・進捗
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/users/{userId}/courses | ユーザーのコース一覧 | 本人 or ADMIN |
| GET | /api/users/{userId}/courses/{courseId}/lessons | 受講コースのレッスン一覧 | 要認証 |
| GET | /api/users/{userId}/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId} | レッスン視聴（進捗記録） | 要認証 |
| PUT | /api/users/{userId}/courses/{courseId}/lesson-groups/{lessonGroupId}/lessons/{lessonId}/completion | レッスン受講完了マーク | 要認証 |

### ユーザー管理
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/users | ユーザー一覧 | ADMIN |
| GET | /api/users/{userId} | ユーザー詳細 | 本人 or ADMIN |
| POST | /api/users | ユーザー作成 | ADMIN |
| PUT | /api/users/{userId} | ユーザー更新 | 本人 or ADMIN |
| DELETE | /api/users/{userId} | ユーザー削除 | ADMIN |
| PUT | /api/users/password | パスワード変更 | 要認証 |
| GET | /api/users/export | ユーザー CSV エクスポート | ADMIN |
| POST | /api/users/import | ユーザー CSV インポート | ADMIN |

### お知らせ
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/news | お知らせ一覧 | 要認証 |
| GET | /api/news/{newsId} | お知らせ詳細 | 要認証 |
| POST | /api/news | お知らせ作成 | ADMIN |
| PUT | /api/news/{newsId} | お知らせ更新 | ADMIN |
| DELETE | /api/news/{newsId} | お知らせ削除 | ADMIN |

### ファイルアップロード
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| POST | /api/files/upload | 画像アップロード | ADMIN |

### 管理者エクスポート
| メソッド | パス | 説明 | 認可 |
|---|---|---|---|
| GET | /api/courses/lessons/export | 全レッスン CSV エクスポート | ADMIN |

## アーキテクチャ

本プロジェクトは **オニオンアーキテクチャ** に基づき、Presentation / Application / Domain / Infrastructure の4層で構成されています。  
依存の方向は **内側（Domain）に向ける** ことを原則とし、ArchUnit テスト（受講生タスクで実装）で自動検証します。

### レイヤー構成

| レイヤー | パッケージ | 責務 |
|---|---|---|
| Presentation | `presentation` | HTTP リクエスト/レスポンス、Controller |
| Application | `application` | ユースケース（ApplicationService）、Command、Dto |
| Domain | `domain` | エンティティ、値オブジェクト、DomainService、Repository インターフェース |
| Infrastructure | `infrastructure` | Repository 実装、Dao、外部サービス連携 |

### ArchUnit ルール一覧（実装時の仕様）

受講生タスクでは、以下のルールを ArchUnit テストとして実装してください。

#### レイヤー依存ルール（Rule 1〜7）

| Rule | 内容 |
|---|---|
| 1 | presentation 層は infrastructure 層にアクセスしてはならない |
| 2 | application 層は infrastructure 層にアクセスしてはならない |
| 3 | application 層は presentation 層にアクセスしてはならない |
| 4 | domain 層は application 層にアクセスしてはならない |
| 5 | domain 層は infrastructure 層にアクセスしてはならない |
| 6 | domain 層は presentation 層にアクセスしてはならない |
| 7 | infrastructure 層は presentation 層にアクセスしてはならない |

#### 命名・構成ルール（Rule 8〜20）

| Rule | 内容 |
|---|---|
| 8 | `presentation.request` パッケージのクラスは `Request` で終わること |
| 9 | `@RestController` 付きクラスは `Controller` で終わること |
| 10 | `application.service` の **インターフェース** は `ApplicationService` で終わること |
| 11 | `application.service` の `@Service` クラスは `ApplicationServiceImpl` で終わること |
| 12 | `application.command` パッケージのクラスは `Command` で終わること |
| 13 | `application.dto` パッケージのクラスは `Dto` で終わること |
| 14 | `domain.repository` の **インターフェース** は `Repository` で終わること |
| 15 | `infrastructure.repository` の `@Repository` クラスは `RepositoryImpl` で終わること |
| 16 | `@RestController` 付きクラスは `@RequestMapping` を持つこと |
| 17 | `@RestController` 付きクラスは Swagger の `@Tag` を持つこと |
| 18 | `infrastructure.repository` のクラス（インターフェース以外）は `@Repository` を持つこと |
| 19 | `domain.service` の **インターフェース** は `DomainService` で終わること |
| 20 | `domain.service` のクラス（インターフェース以外）は `DomainServiceImpl` で終わること |

## プロジェクト構成

```
src/main/java/com/everrefine/elms/
├── presentation/          # コントローラー・リクエスト/レスポンスクラス
├── application/
│   ├── command/           # ユースケース入力
│   ├── dto/               # ユースケース出力
│   ├── exception/         # アプリケーション例外
│   └── service/           # ユースケース実装
├── domain/
│   ├── model/             # エンティティ・値オブジェクト
│   └── repository/        # リポジトリインターフェース
└── infrastructure/
    ├── dao/               # Spring Data JDBC インターフェース
    ├── repository/        # リポジトリ実装
    └── security/          # JWT フィルター・Spring Security 設定
src/main/resources/
├── application.yml        # 共通設定
├── application-dev.yml    # 開発環境設定（ローカルファイルストレージ）
├── application-prd.yml    # 本番環境設定（S3 使用）
└── db/migration/          # Flyway マイグレーションスクリプト
```

## データベースのリセット

開発中にスキーマやシードデータを変え直したい場合は、コンテナごと削除して再起動します。

```bash
docker compose -f docker-compose-dev.yml down -v
docker compose -f docker-compose-dev.yml up -d
./gradlew bootRun
```

## メール確認（Mailpit）

パスワードリセットメールなど、開発環境で送信されたメールは Mailpit でキャッチできます。

```
http://localhost:8025
```

## コードフォーマット

```bash
./gradlew spotlessApply
```

## テスト

```bash
./gradlew test
```

テストは Testcontainers で PostgreSQL コンテナを自動起動して実行します（Docker が必要です）。
