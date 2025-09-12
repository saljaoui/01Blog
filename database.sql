CREATE TABLE "roles"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "name" VARCHAR(255) CHECK
        ("name" IN('USER', 'ADMIN')) NOT NULL,
        "created_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "roles" ADD PRIMARY KEY("id");
ALTER TABLE
    "roles" ADD CONSTRAINT "roles_name_unique" UNIQUE("name");
CREATE TABLE "users"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "email" INTEGER NOT NULL,
    "username" INTEGER NOT NULL,
    "password_hash" TEXT NOT NULL,
    "display_name" TEXT NOT NULL,
    "bio" TEXT NULL,
    "avatar_url" TEXT NULL,
    "is_active" BOOLEAN NOT NULL DEFAULT 'DEFAULT TRUE',
    "is_deleted" BOOLEAN NOT NULL DEFAULT 'DEFAULT FALSE',
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "updated_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "users" ADD PRIMARY KEY("id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_email_unique" UNIQUE("email");
ALTER TABLE
    "users" ADD CONSTRAINT "users_username_unique" UNIQUE("username");
CREATE TABLE "user_roles"(
    "user_id" UUID NOT NULL,
    "role_id" UUID NOT NULL,
    "granted_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "user_roles" ADD PRIMARY KEY("user_id");
ALTER TABLE
    "user_roles" ADD PRIMARY KEY("role_id");
CREATE TABLE "refresh_tokens"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "user_id" UUID NOT NULL,
    "token_hash" TEXT NOT NULL,
    "expires_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL,
        "revoked_at" TIMESTAMP(0)
    WITH
        TIME zone NULL,
        "created_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "refresh_tokens" ADD CONSTRAINT "refresh_tokens_user_id_token_hash_unique" UNIQUE("user_id", "token_hash");
ALTER TABLE
    "refresh_tokens" ADD PRIMARY KEY("id");
CREATE TABLE "subscriptions"(
    "follower_id" UUID NOT NULL,
    "following_id" UUID NOT NULL,
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "subscriptions" ADD PRIMARY KEY("follower_id");
ALTER TABLE
    "subscriptions" ADD PRIMARY KEY("following_id");
CREATE TABLE "posts"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "author_id" UUID NOT NULL,
    "title" TEXT NULL,
    "body_md" TEXT NOT NULL,
    "is_hidden" BOOLEAN NOT NULL DEFAULT 'DEFAULT FALSE',
    "is_deleted" BOOLEAN NOT NULL DEFAULT 'DEFAULT FALSE',
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "updated_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "posts" ADD PRIMARY KEY("id");
CREATE TABLE "post_media"(
    "post_id" UUID NOT NULL,
    "media_id" UUID NOT NULL,
    "position" INTEGER NOT NULL,
    "caption" TEXT NULL
);
ALTER TABLE
    "post_media" ADD PRIMARY KEY("post_id");
ALTER TABLE
    "post_media" ADD PRIMARY KEY("media_id");
CREATE TABLE "post_likes"(
    "post_id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "post_likes" ADD PRIMARY KEY("post_id");
ALTER TABLE
    "post_likes" ADD PRIMARY KEY("user_id");
CREATE TABLE "comments"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "post_id" UUID NOT NULL,
    "author_id" UUID NOT NULL,
    "parent_id" UUID NULL,
    "body_md" TEXT NOT NULL,
    "is_deleted" BOOLEAN NOT NULL DEFAULT 'DEFAULT FALSE',
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "updated_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "comments" ADD PRIMARY KEY("id");
CREATE TABLE "notifications"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "recipient_id" UUID NOT NULL,
    "actor_id" UUID NULL,
    "type" INTEGER NOT NULL,
    "post_id" UUID NULL,
    "comment_id" UUID NULL,
    "is_read" BOOLEAN NOT NULL DEFAULT 'DEFAULT FALSE',
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "read_at" TIMESTAMP(0)
    WITH
        TIME zone NULL
);
ALTER TABLE
    "notifications" ADD PRIMARY KEY("id");
CREATE TABLE "reports"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "reporter_id" UUID NOT NULL,
    "reported_user_id" UUID NULL,
    "reported_post_id" UUID NULL,
    "reason" TEXT NOT NULL,
    "status" INTEGER NOT NULL DEFAULT 'open',
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "reviewed_by" UUID NULL,
        "reviewed_at" TIMESTAMP(0)
    WITH
        TIME zone NULL,
        "resolution_note" TEXT NULL
);
ALTER TABLE
    "reports" ADD PRIMARY KEY("id");
CREATE TABLE "bans"(
    "user_id" UUID NOT NULL,
    "reason" TEXT NOT NULL,
    "banned_until" TIMESTAMP(0) WITH
        TIME zone NULL,
        "created_at" TIMESTAMP(0)
    WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "created_by" UUID NULL
);
ALTER TABLE
    "bans" ADD PRIMARY KEY("user_id");
CREATE TABLE "admin_actions"(
    "id" UUID NOT NULL DEFAULT 'DEFAULT GEN_RANDOM_UUID ( )',
    "admin_id" UUID NOT NULL,
    "action_type" INTEGER NOT NULL,
    "target_user_id" UUID NULL,
    "target_post_id" UUID NULL,
    "reason" TEXT NULL,
    "created_at" TIMESTAMP(0) WITH
        TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE
    "admin_actions" ADD PRIMARY KEY("id");
ALTER TABLE
    "notifications" ADD CONSTRAINT "notifications_actor_id_foreign" FOREIGN KEY("actor_id") REFERENCES "users"("id");
ALTER TABLE
    "posts" ADD CONSTRAINT "posts_id_foreign" FOREIGN KEY("id") REFERENCES "post_likes"("post_id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_id_foreign" FOREIGN KEY("id") REFERENCES "subscriptions"("follower_id");
ALTER TABLE
    "refresh_tokens" ADD CONSTRAINT "refresh_tokens_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("id");
ALTER TABLE
    "admin_actions" ADD CONSTRAINT "admin_actions_admin_id_foreign" FOREIGN KEY("admin_id") REFERENCES "users"("id");
ALTER TABLE
    "admin_actions" ADD CONSTRAINT "admin_actions_target_post_id_foreign" FOREIGN KEY("target_post_id") REFERENCES "posts"("id");
ALTER TABLE
    "roles" ADD CONSTRAINT "roles_id_foreign" FOREIGN KEY("id") REFERENCES "user_roles"("role_id");
ALTER TABLE
    "notifications" ADD CONSTRAINT "notifications_comment_id_foreign" FOREIGN KEY("comment_id") REFERENCES "comments"("id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_id_foreign" FOREIGN KEY("id") REFERENCES "subscriptions"("following_id");
ALTER TABLE
    "comments" ADD CONSTRAINT "comments_parent_id_foreign" FOREIGN KEY("parent_id") REFERENCES "comments"("id");
ALTER TABLE
    "bans" ADD CONSTRAINT "bans_created_by_foreign" FOREIGN KEY("created_by") REFERENCES "users"("id");
ALTER TABLE
    "posts" ADD CONSTRAINT "posts_author_id_foreign" FOREIGN KEY("author_id") REFERENCES "users"("id");
ALTER TABLE
    "reports" ADD CONSTRAINT "reports_reported_post_id_foreign" FOREIGN KEY("reported_post_id") REFERENCES "posts"("id");
ALTER TABLE
    "reports" ADD CONSTRAINT "reports_reporter_id_foreign" FOREIGN KEY("reporter_id") REFERENCES "users"("id");
ALTER TABLE
    "posts" ADD CONSTRAINT "posts_id_foreign" FOREIGN KEY("id") REFERENCES "post_media"("post_id");
ALTER TABLE
    "notifications" ADD CONSTRAINT "notifications_post_id_foreign" FOREIGN KEY("post_id") REFERENCES "posts"("id");
ALTER TABLE
    "notifications" ADD CONSTRAINT "notifications_recipient_id_foreign" FOREIGN KEY("recipient_id") REFERENCES "users"("id");
ALTER TABLE
    "comments" ADD CONSTRAINT "comments_author_id_foreign" FOREIGN KEY("author_id") REFERENCES "users"("id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_id_foreign" FOREIGN KEY("id") REFERENCES "post_likes"("user_id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_id_foreign" FOREIGN KEY("id") REFERENCES "bans"("user_id");
ALTER TABLE
    "reports" ADD CONSTRAINT "reports_reviewed_by_foreign" FOREIGN KEY("reviewed_by") REFERENCES "users"("id");
ALTER TABLE
    "comments" ADD CONSTRAINT "comments_post_id_foreign" FOREIGN KEY("post_id") REFERENCES "posts"("id");
ALTER TABLE
    "reports" ADD CONSTRAINT "reports_reported_user_id_foreign" FOREIGN KEY("reported_user_id") REFERENCES "users"("id");
ALTER TABLE
    "admin_actions" ADD CONSTRAINT "admin_actions_target_user_id_foreign" FOREIGN KEY("target_user_id") REFERENCES "users"("id");
ALTER TABLE
    "users" ADD CONSTRAINT "users_id_foreign" FOREIGN KEY("id") REFERENCES "user_roles"("user_id");