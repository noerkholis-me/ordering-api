
# --- !Ups

create table additional_category (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  master_id                 bigint,
  product_id                bigint,
  additional_image_name     TEXT,
  additional_image_keyword  varchar(255),
  additional_image_title    varchar(255),
  additional_image_description TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_additional_category primary key (id))
;

create table additional_category_master (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  name                      varchar(255),
  slug                      varchar(255),
  sequence                  integer,
  color                     varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_additional_category_master primary key (id))
;

create table address (
  id                        bigint not null,
  is_deleted                boolean,
  is_primary                boolean,
  name                      varchar(255),
  type                      integer,
  phone                     varchar(255),
  address                   varchar(255),
  district_id               bigint,
  township_id               bigint,
  region_id                 bigint,
  village_id                bigint,
  postal_code               varchar(255),
  member_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_address primary key (id))
;

create table article (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  image_header_url          varchar(255),
  image_thumbnail_url       varchar(255),
  image_home_url            varchar(255),
  image_list_url            varchar(255),
  image_name                varchar(255),
  image_title               varchar(255),
  image_alternate           varchar(255),
  image_description         varchar(255),
  view_count                integer,
  content                   TEXT,
  short_description         TEXT,
  status                    varchar(255),
  article_category_id       bigint,
  article_category_name     varchar(255),
  user_id                   bigint,
  change_by                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_article primary key (id))
;

create table article_category (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  sequence                  integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_article_category_name unique (name),
  constraint pk_article_category primary key (id))
;

create table article_comment (
  id                        bigint not null,
  is_deleted                boolean,
  comment_parent_id         bigint,
  article_id                bigint,
  commenter_id              bigint,
  is_admin                  boolean,
  comment                   TEXT,
  is_removed                boolean,
  status                    integer,
  approve_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_article_comment primary key (id))
;

create table attribute (
  id                        bigint not null,
  is_deleted                boolean,
  value                     varchar(255),
  image_url                 varchar(255),
  is_default                boolean,
  odoo_id                   integer,
  additional                varchar(255),
  base_attribute_id         bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_attribute_1 unique (value,base_attribute_id),
  constraint pk_attribute primary key (id))
;

create table bank (
  id                        bigint not null,
  is_deleted                boolean,
  bank_name                 varchar(255),
  account_name              varchar(255),
  account_number            varchar(255),
  description               varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  status                    boolean,
  odoo_id                   integer,
  partner_bank_id           integer,
  account_journal_id        integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_bank primary key (id))
;

create table banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  flash_sale                boolean,
  sequence                  integer,
  sequence_mobile           integer,
  type_id                   integer,
  position_id               integer,
  open_new_tab              boolean,
  link_url                  varchar(255),
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  image_url_mobile          varchar(255),
  banner_mobile_size        varchar(255),
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_banner primary key (id))
;

create table banner_most_popular (
  id                        bigint not null,
  is_deleted                boolean,
  product1_id               bigint,
  image_url1                varchar(255),
  product2_id               bigint,
  image_url2                varchar(255),
  product3_id               bigint,
  image_url3                varchar(255),
  product4_id               bigint,
  image_url4                varchar(255),
  product5_id               bigint,
  image_url5                varchar(255),
  product6_id               bigint,
  image_url6                varchar(255),
  product7_id               bigint,
  image_url7                varchar(255),
  status                    boolean,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_banner_most_popular primary key (id))
;

create table base_attribute (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  type                      varchar(255),
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_base_attribute_name unique (name),
  constraint pk_base_attribute primary key (id))
;

create table blacklist_email (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_blacklist_email_name unique (name),
  constraint pk_blacklist_email primary key (id))
;

create table brand (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  slug                      varchar(255),
  status                    boolean,
  odoo_id                   integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  sequence                  integer,
  user_id                   bigint,
  view_count                integer,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_brand_name unique (name),
  constraint pk_brand primary key (id))
;

create table catalog (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  root_catalog_code         varchar(255),
  is_active                 boolean,
  name                      varchar(255),
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  alias                     varchar(255),
  level                     integer,
  slug                      varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  user_id                   bigint,
  view_count                integer,
  parent_id                 bigint,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_catalog primary key (id))
;

create table catalog2 (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  slug                      varchar(255),
  link_url                  varchar(255),
  meta_title                varchar(255),
  meta_description          varchar(255),
  meta_keyword              varchar(255),
  is_active                 boolean,
  active_from               timestamp,
  active_to                 timestamp,
  sequence                  integer,
  sequence_mobile           integer,
  view_count                integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  image_size                varchar(255),
  lizpedia_id               bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_catalog2 primary key (id))
;

create table catalog_item (
  id                        bigint not null,
  is_deleted                boolean,
  is_active                 boolean,
  active_from               timestamp,
  active_to                 timestamp,
  sequence                  integer,
  sequence_mobile           integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  image_size                varchar(255),
  catalog_id                bigint,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_catalog_item primary key (id))
;

create table category (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  root_category_code        varchar(255),
  is_active                 boolean,
  name                      varchar(255),
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  alias                     varchar(255),
  level                     integer,
  sequence                  integer,
  slug                      varchar(255),
  share_profit              float,
  odoo_id                   integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  image_banner_url          varchar(255),
  image_splash_url          varchar(255),
  user_id                   bigint,
  view_count                integer,
  parent_id                 bigint,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category primary key (id))
;

create table category_banner (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  category_id               bigint,
  color                     varchar(255),
  sequence                  integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category_banner primary key (id))
;

create table category_banner_detail (
  id                        bigint not null,
  is_deleted                boolean,
  sequence                  integer,
  name                      varchar(255),
  caption                   varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  category_banner_id        bigint,
  category_id               bigint,
  sub_category_id           bigint,
  brand_id                  bigint,
  product_id                bigint,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category_banner_detail primary key (id))
;

create table category_banner_menu (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  category_id               bigint,
  color                     varchar(255),
  sequence                  integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category_banner_menu primary key (id))
;

create table category_banner_menu_detail (
  id                        bigint not null,
  is_deleted                boolean,
  sequence                  integer,
  name                      varchar(255),
  caption                   varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  category_banner_id        bigint,
  category_id               bigint,
  sub_category_id           bigint,
  brand_id                  bigint,
  product_id                bigint,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category_banner_menu_detail primary key (id))
;

create table category_promo (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  status                    boolean,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_category_promo_name unique (name),
  constraint pk_category_promo primary key (id))
;

create table change_log (
  id                        varchar(255),
  action_date               timestamp,
  type                      varchar(255),
  user_id                   bigint,
  table_name                varchar(255),
  item_id                   bigint,
  action                    varchar(255),
  before                    TEXT,
  after                     TEXT)
;

create table config_settings (
  id                        bigint not null,
  is_deleted                boolean,
  module                    varchar(255),
  key                       varchar(255),
  name                      varchar(255),
  value                     varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_config_settings_key unique (key),
  constraint pk_config_settings primary key (id))
;

create table courier (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  code                      varchar(255),
  type                      integer,
  divider                   float,
  delivery_type             varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  odoo_id                   integer,
  product_odoo_id           integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_courier primary key (id))
;

create table courier_point_location (
  id                        bigint not null,
  is_deleted                boolean,
  point_name                varchar(255),
  point_address             varchar(255),
  agent_id                  integer,
  longitude                 float,
  latitude                  float,
  township_id               bigint,
  courier_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_courier_point_location primary key (id))
;

create table courier_service (
  id                        bigint not null,
  is_deleted                boolean,
  service                   varchar(255),
  courier_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_courier_service primary key (id))
;

create table currency (
  code                      varchar(255) not null,
  code_display              varchar(255),
  name                      varchar(255),
  sequence                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_currency primary key (code))
;

create table currency_exchange_rate (
  date                      timestamp,
  code                      varchar(255),
  rate                      float,
  created_at                timestamp not null,
  updated_at                timestamp not null)
;

create table district (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  name                      varchar(255),
  region_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_district primary key (id))
;

create table faq (
  id                        bigint not null,
  is_deleted                boolean,
  title                     varchar(255),
  name                      varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  content                   TEXT,
  sequence                  integer,
  faq_group_id              bigint,
  type                      integer,
  user_id                   bigint,
  view_count                integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_faq primary key (id))
;

create table faq_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_faq_group_name unique (name),
  constraint pk_faq_group primary key (id))
;

create table feature (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  key                       varchar(255),
  section                   varchar(255),
  description               varchar(255),
  is_active                 boolean,
  sequence                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_feature_key unique (key),
  constraint pk_feature primary key (id))
;

create table footer (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  position                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  page_url                  varchar(255),
  static_page_id            bigint,
  sequence                  integer,
  new_tab                   boolean,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_footer primary key (id))
;

create table highlight_banner (
  id                        bigint not null,
  is_deleted                boolean,
  is_active                 boolean,
  name                      varchar(255),
  slug                      varchar(255) not null,
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  level                     integer,
  sequence                  integer,
  parent_id                 bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_highlight_banner_slug unique (slug),
  constraint pk_highlight_banner primary key (id))
;

create table images (
  id                        varchar(255) not null,
  images                    TEXT,
  constraint pk_images primary key (id))
;

create table information_category_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  module_type               varchar(255),
  slug                      varchar(255),
  sequence                  integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_information_category_group primary key (id))
;

create table liz_pedia (
  id                        bigint not null,
  is_deleted                boolean,
  content                   TEXT,
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  slug                      varchar(255),
  name                      varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_liz_pedia primary key (id))
;

create table loyalty (
  id                        bigint not null,
  is_deleted                boolean,
  content                   TEXT,
  name                      varchar(255),
  slug                      varchar(255),
  loyalty_image_name        TEXT,
  loyalty_image_keyword     varchar(255),
  loyalty_image_title       varchar(255),
  loyalty_image_description TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  image_url_mobile          varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_loyalty primary key (id))
;

create table master_color (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  slug                      varchar(255),
  color                     varchar(255),
  image_url                 varchar(255),
  image_name                varchar(255),
  image_title               varchar(255),
  image_alt                 varchar(255),
  image_description         TEXT,
  is_default                boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_color primary key (id))
;

create table member (
  id                        bigint not null,
  is_deleted                boolean,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  full_name                 varchar(255),
  email                     varchar(255),
  username                  varchar(255),
  email_notifikasi          varchar(255),
  thumbnail_image_url       varchar(255),
  medium_image_url          varchar(255),
  large_image_url           varchar(255),
  phone                     varchar(255),
  gender                    varchar(1),
  birth_date                timestamp,
  billing_address_id        varchar(255),
  facebook_user_id          varchar(255),
  google_user_id            varchar(255),
  activation_code           varchar(255),
  is_active                 boolean,
  news_letter               boolean,
  reset_token               varchar(255),
  reset_time                bigint,
  code_expire               timestamp,
  last_login                timestamp,
  last_purchase             timestamp,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_member_email unique (email),
  constraint uq_member_username unique (username),
  constraint uq_member_phone unique (phone),
  constraint uq_member_facebook_user_id unique (facebook_user_id),
  constraint uq_member_google_user_id unique (google_user_id),
  constraint pk_member primary key (id))
;

create table member_log (
  id                        bigint not null,
  is_deleted                boolean,
  member_type               varchar(255),
  is_active                 boolean,
  token                     varchar(255),
  expired_date              timestamp,
  device_model              varchar(255),
  device_type               varchar(255),
  device_id                 varchar(255),
  api_key                   varchar(255),
  member_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_member_log primary key (id))
;

create table merchant (
  id                        bigint not null,
  is_deleted                boolean,
  password                  varchar(255),
  email                     varchar(255),
  birth_date                timestamp,
  gender                    varchar(1),
  full_name                 varchar(255),
  domain                    varchar(255),
  account_number            varchar(255),
  account_alias             varchar(255),
  own_merchant              boolean,
  merchant_code             varchar(255),
  name                      varchar(255),
  logo                      varchar(255),
  display                   boolean,
  company_name              varchar(255),
  status                    varchar(255),
  city_name                 varchar(255),
  postal_code               varchar(255),
  province                  varchar(255),
  commission_type           varchar(255),
  address                   varchar(255),
  phone                     varchar(255),
  meta_description          varchar(255),
  story                     varchar(255),
  url                       varchar(255),
  merchant_url_page         varchar(255),
  anchor                    boolean,
  url_banner                varchar(255),
  quick_response            bigint,
  product_availability      bigint,
  product_quality           bigint,
  rating                    float,
  count_rating              integer,
  product_handled_and_shipped_description varchar(255),
  product_handled_description varchar(255),
  product_shipped_description varchar(255),
  activation_code           varchar(255),
  is_active                 boolean,
  district_id               bigint,
  township_id               bigint,
  region_id                 bigint,
  village_id                bigint,
  courier_point_location_id bigint,
  user_id                   bigint,
  reset_token               varchar(255),
  reset_time                bigint,
  code_expire               timestamp,
  odoo_id                   integer,
  balance                   float,
  unpaid_customer           float,
  unpaid_hokeba             float,
  paid_hokeba               float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_merchant_email unique (email),
  constraint uq_merchant_domain unique (domain),
  constraint pk_merchant primary key (id))
;

create table merchant_log (
  id                        bigint not null,
  is_deleted                boolean,
  member_type               varchar(255),
  is_active                 boolean,
  token                     varchar(255),
  expired_date              timestamp,
  device_model              varchar(255),
  device_type               varchar(255),
  device_id                 varchar(255),
  api_key                   varchar(255),
  merchant_id               bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_merchant_log primary key (id))
;

create table merchant_promo_request (
  id                        bigint not null,
  is_deleted                boolean,
  promo_id                  bigint,
  merchant_id               bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_merchant_promo_request primary key (id))
;

create table merchant_promo_request_product (
  id                        bigint not null,
  is_deleted                boolean,
  request_id                bigint,
  product_id                bigint,
  status                    varchar(255),
  stock                     integer,
  price                     float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_merchant_promo_request_produc primary key (id))
;

create table mobile_version (
  id                        bigint not null,
  is_deleted                boolean,
  mobile_version            integer,
  description               varchar(255),
  url_android               varchar(255),
  url_ios                   varchar(255),
  major_minor_update        boolean,
  release_date              timestamp,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_mobile_version primary key (id))
;

create table most_popular_banner (
  id                        bigint not null,
  is_deleted                boolean,
  sequence                  integer,
  name                      varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  category_id               bigint,
  brand_id                  bigint,
  product_id                bigint,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_most_popular_banner primary key (id))
;

create table notification_member (
  id                        bigint not null,
  is_deleted                boolean,
  member_id                 bigint,
  tipe                      integer,
  title                     varchar(255),
  content                   varchar(255),
  is_read                   boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  date                      timestamp not null,
  constraint pk_notification_member primary key (id))
;

create table notification_merchant (
  id                        bigint not null,
  is_deleted                boolean,
  merchant_id               bigint,
  tipe                      integer,
  title                     varchar(255),
  content                   varchar(255),
  is_read                   boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  date                      timestamp not null,
  constraint pk_notification_merchant primary key (id))
;

create table param (
  param                     varchar(255),
  code                      varchar(255),
  value                     varchar(255))
;

create table partner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  slug                      varchar(255),
  status                    boolean,
  odoo_id                   integer,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  sequence                  integer,
  user_id                   bigint,
  view_count                integer,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_partner_name unique (name),
  constraint pk_partner primary key (id))
;

create table payment_expiration (
  id                        bigint not null,
  is_deleted                boolean,
  type                      varchar(255),
  total                     integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_payment_expiration primary key (id))
;

create table photo (
  id                        bigint not null,
  is_deleted                boolean,
  file_name                 varchar(255),
  file_name_before          varchar(255),
  full_url                  varchar(255),
  medium_url                varchar(255),
  thumb_url                 varchar(255),
  blur_url                  varchar(255),
  user_id                   bigint,
  user_type                 varchar(255),
  module                    varchar(255),
  module_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_photo primary key (id))
;

create table product (
  id                        bigint not null,
  is_deleted                boolean,
  sku                       varchar(255),
  sku_seller                varchar(255),
  name                      varchar(255),
  slug                      varchar(255),
  product_type              integer,
  is_new                    boolean,
  status                    boolean,
  meta_title                varchar(255),
  meta_description          varchar(255),
  meta_keyword              varchar(255),
  buy_price                 float,
  price                     float,
  currency_cd               varchar(255),
  view_count                integer,
  retur                     integer,
  stock                     boolean,
  item_count                bigint,
  item_count_odoo           bigint,
  strike_through_display    float,
  price_display             float,
  discount                  float,
  discount_type             integer default 0,
  thumbnail_url             varchar(255),
  image_url                 varchar(255),
  position                  integer,
  is_show                   boolean default true,
  short_descriptions        text,
  description               text,
  weight                    float,
  dimension1                float,
  dimension2                float,
  dimension3                float,
  diameter                  float,
  number_of_diamond         float,
  diamond_color             varchar(255),
  diamond_clarity           varchar(255),
  stamp                     varchar(255),
  certificate               varchar(255),
  kadar                     float,
  weight_of_gold            float,
  sum_carat_of_gold         float,
  weight_of_gold_plus_diamond float,
  warranty_type             integer default 0,
  warranty_period           integer default 0,
  sold_fulfilled_by         varchar(255),
  what_in_the_box           text,
  full_image_urls           TEXT,
  medium_image_urls         TEXT,
  thumbnail_image_urls      TEXT,
  blur_image_urls           TEXT,
  threesixty_image_urls     TEXT,
  size_guide                text,
  odoo_id                   integer,
  merchant_id               bigint,
  vendor_id                 bigint,
  brand_id                  bigint,
  category_id               bigint,
  parent_category_id        bigint,
  grand_parent_category_id  bigint,
  average_rating            float,
  count_rating              integer,
  num_of_order              integer,
  discount_active_from      timestamp,
  discount_active_to        timestamp,
  product_group_id          bigint,
  product_variant_group_id  bigint,
  user_id                   bigint,
  first_po_status           integer,
  approved_status           varchar(255),
  approved_note             varchar(2000),
  approved_information      varchar(1000),
  approved_by_id            bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_product_sku unique (sku),
  constraint pk_product primary key (id))
;

create table product_detail (
  id                        bigint not null,
  is_deleted                boolean,
  product_name              varchar(255),
  short_descriptions        text,
  description               text,
  weight                    float,
  dimension1                float,
  dimension2                float,
  dimension3                float,
  diameter                  float,
  number_of_diamond         float,
  diamond_color             varchar(255),
  diamond_clarity           varchar(255),
  stamp                     varchar(255),
  certificate               varchar(255),
  kadar                     float,
  weight_of_gold            float,
  sum_carat_of_gold         float,
  weight_of_gold_plus_diamond float,
  warranty_type             integer default 0,
  warranty_period           integer default 0,
  sold_fulfilled_by         varchar(255),
  what_in_the_box           text,
  total_stock               bigint,
  free_stock                bigint,
  reserved_stock            bigint,
  stock                     boolean,
  limited_stock             boolean,
  stock_counter             boolean,
  published                 boolean,
  full_image_urls           TEXT,
  medium_image_urls         TEXT,
  thumbnail_image_urls      TEXT,
  blur_image_urls           TEXT,
  threesixty_image_urls     TEXT,
  size_guide                text,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_detail primary key (id))
;

create table product_detail_tmp (
  id_tmp                    varchar(255) not null,
  id                        bigint,
  product_name              varchar(255),
  short_descriptions        varchar(255),
  description               text,
  weight                    float,
  dimension1                float,
  dimension2                float,
  dimension3                float,
  diameter                  float,
  number_of_diamond         float,
  diamond_color             varchar(255),
  diamond_clarity           varchar(255),
  stamp                     varchar(255),
  certificate               varchar(255),
  kadar                     float,
  weight_of_gold            float,
  sum_carat_of_gold         float,
  weight_of_gold_plus_diamond float,
  warranty_type             integer default 0,
  warranty_period           integer default 0,
  sold_fulfilled_by         varchar(255),
  what_in_the_box           varchar(255),
  total_stock               bigint,
  free_stock                bigint,
  reserved_stock            bigint,
  stock                     boolean,
  limited_stock             boolean,
  stock_counter             boolean,
  published                 boolean,
  full_image_urls           TEXT,
  medium_image_urls         TEXT,
  thumbnail_image_urls      TEXT,
  blur_image_urls           TEXT,
  threesixty_image_urls     TEXT,
  size_guide                text,
  product_id                varchar(255),
  constraint pk_product_detail_tmp primary key (id_tmp))
;

create table product_detail_variance (
  id                        bigint not null,
  is_deleted                boolean,
  sku                       varchar(255),
  total_stock               bigint,
  free_stock                bigint,
  reserved_stock            bigint,
  stock                     boolean,
  limited_stock             boolean,
  stock_counter             boolean,
  product_id                bigint,
  color_id                  bigint,
  size_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_detail_variance primary key (id))
;

create table product_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  lowest_price_product      bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_group primary key (id))
;

create table product_review (
  id                        bigint not null,
  is_deleted                boolean,
  title                     varchar(255),
  comment                   varchar(255),
  rating                    integer,
  is_active                 boolean,
  approved_status           varchar(255),
  approved_by_id            bigint,
  image_url                 varchar(255),
  member_id                 bigint,
  merchant_id               bigint,
  user_id                   bigint,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_review primary key (id))
;

create table product_stock_tmp (
  id_tmp                    varchar(255) not null,
  product_id                bigint,
  stock                     bigint,
  odoo_id                   integer,
  approved_status           varchar(255),
  approved_note             varchar(255),
  approved_by_id            bigint,
  updated_at                timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  constraint pk_product_stock_tmp primary key (id_tmp))
;

create table product_tmp (
  id_tmp                    varchar(255) not null,
  id                        bigint,
  sku                       varchar(255),
  name                      varchar(255),
  product_type              integer,
  is_new                    boolean,
  status                    boolean,
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  buy_price                 float,
  price                     float,
  currency_cd               varchar(255),
  view_count                integer,
  stock                     boolean,
  item_count                bigint,
  strike_through_display    float,
  price_display             float,
  discount                  float,
  discount_type             integer default 0,
  thumbnail_url             varchar(255),
  image_url                 varchar(255),
  odoo_id                   integer,
  merchant_id               bigint,
  vendor_id                 bigint,
  brand_id                  bigint,
  category_id               bigint,
  parent_category_id        bigint,
  grand_parent_category_id  bigint,
  detail_id                 varchar(255),
  average_rating            float,
  count_rating              integer,
  discount_active_from      timestamp,
  discount_active_to        timestamp,
  product_group_id          bigint,
  user_id                   bigint,
  first_po_status           integer,
  approved_status           varchar(255),
  approved_note             varchar(255),
  approved_by_id            bigint,
  updated_at                timestamp,
  is_show                   boolean default true,
  created_at                timestamp not null,
  constraint pk_product_tmp primary key (id_tmp))
;

create table product_variant_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  lowest_price_product      bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_variant_group primary key (id))
;

create table promo (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  all_seller                boolean,
  link_url                  varchar(255),
  promo_image_name          TEXT,
  promo_image_keyword       varchar(255),
  promo_image_title         varchar(255),
  promo_image_description   TEXT,
  image_url                 varchar(255),
  promo_size                varchar(255),
  image_url_responsive      varchar(255),
  promo_responsive_size     varchar(255),
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_promo primary key (id))
;

create table purchase_order (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  total                     float,
  received_at               timestamp,
  status                    integer,
  information               varchar(255),
  odoo_id                   integer,
  merchant_id               bigint,
  vendor_id                 bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_purchase_order_code unique (code),
  constraint pk_purchase_order primary key (id))
;

create table purchase_order_detail (
  id                        bigint not null,
  is_deleted                boolean,
  po_id                     bigint,
  product_id                bigint,
  qty                       integer,
  price                     float,
  sub_total                 float,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_purchase_order_detail primary key (id))
;

create table purchase_order_return (
  id                        bigint not null,
  is_deleted                boolean,
  return_number             varchar(30),
  vendor_id                 bigint,
  purchase_order_id         bigint,
  date                      timestamp,
  document_no               varchar(255),
  type                      varchar(1),
  status                    varchar(1),
  description               varchar(255),
  approved_note             varchar(255),
  approved_by_id            bigint,
  user_id                   bigint,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_purchase_order_return_return_ unique (return_number),
  constraint pk_purchase_order_return primary key (id))
;

create table purchase_order_return_detail (
  id                        bigint not null,
  is_deleted                boolean,
  purchase_order_return_id  bigint,
  product_id                bigint,
  quantity                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_purchase_order_return_detail primary key (id))
;

create table region (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  name                      varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_region primary key (id))
;

create table role (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  key                       varchar(255),
  description               varchar(255),
  is_active                 boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_role_key unique (key),
  constraint pk_role primary key (id))
;

create table role_feature (
  feature_id                bigint,
  role_id                   bigint,
  access                    integer)
;

create table sms_blast (
  id                        bigint not null,
  is_deleted                boolean,
  title                     varchar(255),
  recipient                 varchar(255),
  filter_recipient          varchar(255),
  date                      timestamp,
  content                   varchar(255),
  user_id                   bigint,
  template_id               varchar(255),
  is_sent                   boolean,
  sent_at                   timestamp,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sms_blast primary key (id))
;

create table sms_template (
  id                        varchar(255) not null,
  name                      varchar(255),
  subject                   varchar(255),
  content                   varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sms_template primary key (id))
;

create table sales_order (
  id                        bigint not null,
  is_deleted                boolean,
  order_number              varchar(255),
  discount                  float,
  voucher                   float,
  subtotal                  float,
  shipping                  float,
  total_price               float,
  member_id                 bigint,
  shipment_address_id       bigint,
  courier_point_location_id bigint,
  courier_id                bigint,
  billing_address_id        bigint,
  bank_id                   bigint,
  status                    varchar(255),
  expired_date              timestamp,
  struct                    varchar(255),
  shipment_type             varchar(255),
  payment_type              varchar(255),
  email_notif               varchar(255),
  approved_date             timestamp,
  approved_by_id            bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  order_date                timestamp not null,
  constraint uq_sales_order_order_number unique (order_number),
  constraint pk_sales_order primary key (id))
;

create table sales_order_detail (
  id                        bigint not null,
  is_deleted                boolean,
  product_id                bigint,
  product_var_id            bigint,
  status                    varchar(255),
  merchant_id               bigint,
  vendor_id                 bigint,
  sales_order_id            bigint,
  sales_order_seller_id     bigint,
  product_name              varchar(255),
  fashion_size              varchar(255),
  fashion_size_id           bigint,
  price                     float,
  price_discount            float,
  quantity                  integer,
  discount_persen           float,
  discount_amount           float,
  sub_total                 float,
  total_price               float,
  tax                       float,
  tax_price                 float,
  voucher                   float,
  payment_seller            float,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_detail primary key (id))
;

create table sales_order_payment (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  confirm_at                timestamp,
  void_at                   timestamp,
  confirm_by_id             bigint,
  total_transfer            float,
  invoice_no                varchar(255),
  debit_account_name        varchar(255),
  debit_account_number      varchar(255),
  image_url                 varchar(255),
  comments                  varchar(255),
  status                    varchar(255),
  transaction_id            varchar(255),
  eci_code                  varchar(255),
  payment_instalment        varchar(255),
  va_number                 varchar(255),
  company_code              varchar(255),
  settlement                boolean,
  payment_type              varchar(255),
  bank                      varchar(255),
  card_type                 varchar(255),
  instalment_cost           float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_sales_order_payment_invoice_n unique (invoice_no),
  constraint pk_sales_order_payment primary key (id))
;

create table sales_order_return (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  sales_order_seller_id     bigint,
  sales_order_return_group_id bigint,
  return_number             varchar(30),
  member_id                 bigint,
  date                      timestamp,
  document_no               varchar(255),
  type                      varchar(1),
  status                    varchar(1),
  request_at                timestamp,
  description               varchar(255),
  schedule_at               timestamp,
  send_at                   timestamp,
  note                      varchar(255),
  user_id                   bigint,
  odoo_id                   integer,
  pengeluaran_odoo_id       integer,
  approved_by               varchar(255),
  rejected_by               varchar(255),
  shipped_by                varchar(255),
  delivered_by              varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_sales_order_return_return_num unique (return_number),
  constraint pk_sales_order_return primary key (id))
;

create table sales_order_return_detail (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_return_id     bigint,
  product_id                bigint,
  quantity                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_return_detail primary key (id))
;

create table sales_order_return_group (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  return_number             varchar(30),
  member_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_sales_order_return_group_retu unique (return_number),
  constraint pk_sales_order_return_group primary key (id))
;

create table sales_order_seller (
  id                        bigint not null,
  is_deleted                boolean,
  order_number              varchar(255),
  merchant_id               bigint,
  vendor_id                 bigint,
  sales_order_id            bigint,
  discount                  float,
  voucher                   float,
  voucher_id                bigint,
  subtotal                  float,
  weights                   float,
  volumes                   float,
  retur_amount              float,
  total_price               float,
  courier_id                bigint,
  courier_code              varchar(255),
  courier_name              varchar(255),
  courier_service_code      varchar(255),
  courier_service_name      varchar(255),
  shipping_cost_detail_id   bigint,
  status                    varchar(255),
  member_id                 bigint,
  shipment_address_id       bigint,
  courier_point_location_id bigint,
  sent_date                 timestamp,
  delivered_date            timestamp,
  tracking_number           varchar(255),
  odoo_id                   integer,
  vendor_odoo_id            integer,
  invoice_odoo_id           integer,
  invoice_vendor_odoo_id    integer,
  shipping                  float,
  payment_status            integer,
  payment_seller            float,
  payment_date              timestamp,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  order_date                timestamp not null,
  constraint uq_sales_order_seller_order_numb unique (order_number),
  constraint pk_sales_order_seller primary key (id))
;

create table sales_order_seller_status (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_seller_id     bigint,
  date                      timestamp,
  description               varchar(255),
  type                      integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_seller_status primary key (id))
;

create table seller_review (
  id                        bigint not null,
  is_deleted                boolean,
  title                     varchar(255),
  comment                   varchar(255),
  rating                    integer,
  is_active                 boolean,
  approved_status           varchar(255),
  approved_by_id            bigint,
  image_url                 varchar(255),
  member_id                 bigint,
  merchant_id               bigint,
  vendor_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_seller_review primary key (id))
;

create table seo_page (
  id                        bigint not null,
  is_deleted                boolean,
  content                   TEXT,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_seo_page primary key (id))
;

create table settlement (
  id                        bigint not null,
  is_deleted                boolean,
  start_date                timestamp,
  end_date                  timestamp,
  status_read               boolean,
  status_print              boolean,
  status_complete           boolean,
  total_merchant            integer,
  total_settlement          float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_settlement primary key (id))
;

create table settlement_detail (
  id                        bigint not null,
  is_deleted                boolean,
  account_number            varchar(255),
  account_alias             varchar(255),
  amount                    float,
  merchant_id               bigint,
  settlement_id             bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_settlement_detail primary key (id))
;

create table shipping_city (
  id                        bigint not null,
  is_deleted                boolean,
  region_id                 bigint,
  district_id               bigint,
  township_id               bigint,
  village_id                bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_shipping_city primary key (id))
;

create table shipping_cost (
  id                        bigint not null,
  is_deleted                boolean,
  courier_id                bigint,
  township_from_id          bigint,
  township_to_id            bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_shipping_cost primary key (id))
;

create table shipping_cost_detail (
  id                        bigint not null,
  is_deleted                boolean,
  shipping_cost_id          bigint not null,
  service_id                bigint,
  description               varchar(255),
  cost                      float,
  estimated_time_delivery   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_shipping_cost_detail primary key (id))
;

create table fashion_size (
  id                        bigint not null,
  is_deleted                boolean,
  international             varchar(255),
  eu                        integer,
  sequence                  integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_fashion_size primary key (id))
;

create table page (
  id                        bigint not null,
  is_deleted                boolean,
  content                   TEXT,
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  slug                      varchar(255),
  name                      varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_page primary key (id))
;

create table sub_category_banner (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  category_id               bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sub_category_banner primary key (id))
;

create table sub_category_banner_detail (
  id                        bigint not null,
  is_deleted                boolean,
  sequence                  integer,
  name                      varchar(255),
  caption                   varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  sub_category_banner_id    bigint,
  brand_id                  bigint,
  image_url                 varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sub_category_banner_detail primary key (id))
;

create table tag (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_tag_name unique (name),
  constraint pk_tag primary key (id))
;

create table township (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  name                      varchar(255),
  district_id               bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_township primary key (id))
;

create table user_cms (
  id                        bigint not null,
  is_deleted                boolean,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  full_name                 varchar(255),
  phone                     varchar(255),
  gender                    varchar(1),
  birth_date                timestamp,
  activation_code           varchar(255),
  is_active                 boolean,
  role_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_user_cms_email unique (email),
  constraint pk_user_cms primary key (id))
;

create table user_log (
  id                        bigint not null,
  is_deleted                boolean,
  user_type                 varchar(255),
  is_active                 boolean,
  token                     varchar(255),
  expired_date              timestamp,
  device_model              varchar(255),
  device_type               varchar(255),
  api_key                   varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_user_log primary key (id))
;

create table vendor (
  id                        bigint not null,
  is_deleted                boolean,
  full_name                 varchar(255),
  code                      varchar(255),
  name                      varchar(255),
  status                    boolean,
  rating                    float,
  count_rating              integer,
  address                   varchar(255),
  phone                     varchar(255),
  city_name                 varchar(255),
  postal_code               varchar(255),
  province                  varchar(255),
  email                     varchar(255),
  logo                      varchar(255),
  odoo_id                   integer,
  unpaid_customer           float,
  unpaid_hokeba             float,
  paid_hokeba               float,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_vendor primary key (id))
;

create table village (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  name                      varchar(255),
  township_id               bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_village primary key (id))
;

create table voucher (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  description               varchar(255),
  masking                   varchar(255),
  type                      varchar(255),
  status                    boolean,
  discount                  float,
  discount_type             integer default 0,
  count                     integer,
  max_value                 float,
  min_purchase              float,
  priority                  integer,
  stop_further_rule_porcessing integer,
  valid_from                timestamp,
  valid_to                  timestamp,
  filter_status             varchar(255),
  assigned_to               varchar(255),
  created_by                bigint,
  updated_by                bigint,
  merchant_by               bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_voucher primary key (id))
;

create table voucher_detail (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  order_number              varchar(255),
  voucher_id                bigint,
  status                    integer default 0,
  member_id                 bigint,
  used_at                   timestamp,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_voucher_detail primary key (id))
;

create table wish_list (
  id                        bigint not null,
  is_deleted                boolean,
  member_id                 bigint,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_wish_list primary key (id))
;


create table banner_merchant (
  banner_id                      bigint not null,
  merchant_id                    bigint not null,
  constraint pk_banner_merchant primary key (banner_id, merchant_id))
;

create table banner_category (
  banner_id                      bigint not null,
  category_id                    bigint not null,
  constraint pk_banner_category primary key (banner_id, category_id))
;

create table banner_product (
  banner_id                      bigint not null,
  product_id                     bigint not null,
  constraint pk_banner_product primary key (banner_id, product_id))
;

create table banner_most_popular_merchant (
  banner_most_popular_id         bigint not null,
  merchant_id                    bigint not null,
  constraint pk_banner_most_popular_merchant primary key (banner_most_popular_id, merchant_id))
;

create table banner_most_popular_category (
  banner_most_popular_id         bigint not null,
  category_id                    bigint not null,
  constraint pk_banner_most_popular_category primary key (banner_most_popular_id, category_id))
;

create table catalog_base_attribute (
  catalog_id                     bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_catalog_base_attribute primary key (catalog_id, base_attribute_id))
;

create table category_base_attribute (
  category_id                    bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_category_base_attribute primary key (category_id, base_attribute_id))
;

create table highlight_banner_merchant (
  highlight_banner_id            bigint not null,
  merchant_id                    bigint not null,
  constraint pk_highlight_banner_merchant primary key (highlight_banner_id, merchant_id))
;

create table highlight_banner_brand (
  highlight_banner_id            bigint not null,
  brand_id                       bigint not null,
  constraint pk_highlight_banner_brand primary key (highlight_banner_id, brand_id))
;

create table highlight_banner_category (
  highlight_banner_id            bigint not null,
  category_id                    bigint not null,
  constraint pk_highlight_banner_category primary key (highlight_banner_id, category_id))
;

create table highlight_banner_product (
  highlight_banner_id            bigint not null,
  product_id                     bigint not null,
  constraint pk_highlight_banner_product primary key (highlight_banner_id, product_id))
;

create table merchant_courier (
  merchant_id                    bigint not null,
  courier_id                     bigint not null,
  constraint pk_merchant_courier primary key (merchant_id, courier_id))
;

create table product_base_attribute (
  product_id                     bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_product_base_attribute primary key (product_id, base_attribute_id))
;

create table product_attribute (
  product_id                     bigint not null,
  attribute_id                   bigint not null,
  constraint pk_product_attribute primary key (product_id, attribute_id))
;

create table product_fashion_size (
  product_id                     bigint not null,
  fashion_size_id                bigint not null,
  constraint pk_product_fashion_size primary key (product_id, fashion_size_id))
;

create table product_tmp_base_attribute (
  product_tmp_id_tmp             varchar(255) not null,
  base_attribute_id              bigint not null,
  constraint pk_product_tmp_base_attribute primary key (product_tmp_id_tmp, base_attribute_id))
;

create table product_tmp_attribute (
  product_tmp_id_tmp             varchar(255) not null,
  attribute_id                   bigint not null,
  constraint pk_product_tmp_attribute primary key (product_tmp_id_tmp, attribute_id))
;

create table product_tmp_fashion_size (
  product_tmp_id_tmp             varchar(255) not null,
  fashion_size_id                bigint not null,
  constraint pk_product_tmp_fashion_size primary key (product_tmp_id_tmp, fashion_size_id))
;

create table product_variant_group_base_attri (
  product_variant_group_id       bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_product_variant_group_base_attri primary key (product_variant_group_id, base_attribute_id))
;

create table promo_merchant (
  promo_id                       bigint not null,
  merchant_id                    bigint not null,
  constraint pk_promo_merchant primary key (promo_id, merchant_id))
;

create table promo_brand (
  promo_id                       bigint not null,
  brand_id                       bigint not null,
  constraint pk_promo_brand primary key (promo_id, brand_id))
;

create table promo_category (
  promo_id                       bigint not null,
  category_id                    bigint not null,
  constraint pk_promo_category primary key (promo_id, category_id))
;

create table promo_product (
  promo_id                       bigint not null,
  product_id                     bigint not null,
  constraint pk_promo_product primary key (promo_id, product_id))
;

create table sms_blast_member (
  sms_blast_id                   bigint not null,
  member_id                      bigint not null,
  constraint pk_sms_blast_member primary key (sms_blast_id, member_id))
;

create table sales_order_detail_voucher_detai (
  sales_order_detail_id          bigint not null,
  voucher_detail_id              bigint not null,
  constraint pk_sales_order_detail_voucher_detai primary key (sales_order_detail_id, voucher_detail_id))
;

create table sub_category_banner_detail_produ (
  sub_category_banner_detail_id  bigint not null,
  product_id                     bigint not null,
  constraint pk_sub_category_banner_detail_produ primary key (sub_category_banner_detail_id, product_id))
;

create table tag_article (
  tag_id                         bigint not null,
  article_id                     bigint not null,
  constraint pk_tag_article primary key (tag_id, article_id))
;

create table tag_product (
  tag_id                         bigint not null,
  product_id                     bigint not null,
  constraint pk_tag_product primary key (tag_id, product_id))
;

create table voucher_merchant (
  voucher_id                     bigint not null,
  merchant_id                    bigint not null,
  constraint pk_voucher_merchant primary key (voucher_id, merchant_id))
;

create table voucher_brand (
  voucher_id                     bigint not null,
  brand_id                       bigint not null,
  constraint pk_voucher_brand primary key (voucher_id, brand_id))
;

create table voucher_category (
  voucher_id                     bigint not null,
  category_id                    bigint not null,
  constraint pk_voucher_category primary key (voucher_id, category_id))
;

create table voucher_product (
  voucher_id                     bigint not null,
  product_id                     bigint not null,
  constraint pk_voucher_product primary key (voucher_id, product_id))
;

create table voucher_member (
  voucher_id                     bigint not null,
  member_id                      bigint not null,
  constraint pk_voucher_member primary key (voucher_id, member_id))
;
create sequence additional_category_seq;

create sequence additional_category_master_seq;

create sequence address_seq;

create sequence article_seq;

create sequence article_category_seq;

create sequence article_comment_seq;

create sequence attribute_seq;

create sequence bank_seq;

create sequence banner_seq;

create sequence banner_most_popular_seq;

create sequence base_attribute_seq;

create sequence blacklist_email_seq;

create sequence brand_seq;

create sequence catalog_seq;

create sequence catalog2_seq;

create sequence catalog_item_seq;

create sequence category_seq;

create sequence category_banner_seq;

create sequence category_banner_detail_seq;

create sequence category_banner_menu_seq;

create sequence category_banner_menu_detail_seq;

create sequence category_promo_seq;

create sequence config_settings_seq;

create sequence courier_seq;

create sequence courier_point_location_seq;

create sequence courier_service_seq;

create sequence currency_seq;

create sequence district_seq;

create sequence faq_seq;

create sequence faq_group_seq;

create sequence feature_seq;

create sequence footer_seq;

create sequence highlight_banner_seq;

create sequence images_seq;

create sequence information_category_group_seq;

create sequence liz_pedia_seq;

create sequence loyalty_seq;

create sequence master_color_seq;

create sequence member_seq;

create sequence member_log_seq;

create sequence merchant_seq;

create sequence merchant_log_seq;

create sequence merchant_promo_request_seq;

create sequence merchant_promo_request_product_seq;

create sequence mobile_version_seq;

create sequence most_popular_banner_seq;

create sequence notification_member_seq;

create sequence notification_merchant_seq;

create sequence partner_seq;

create sequence payment_expiration_seq;

create sequence photo_seq;

create sequence product_seq;

create sequence product_detail_seq;

create sequence product_detail_tmp_seq;

create sequence product_detail_variance_seq;

create sequence product_group_seq;

create sequence product_review_seq;

create sequence product_stock_tmp_seq;

create sequence product_tmp_seq;

create sequence product_variant_group_seq;

create sequence promo_seq;

create sequence purchase_order_seq;

create sequence purchase_order_detail_seq;

create sequence purchase_order_return_seq;

create sequence purchase_order_return_detail_seq;

create sequence region_seq;

create sequence role_seq;

create sequence sms_blast_seq;

create sequence sms_template_seq;

create sequence sales_order_seq;

create sequence sales_order_detail_seq;

create sequence sales_order_payment_seq;

create sequence sales_order_return_seq;

create sequence sales_order_return_detail_seq;

create sequence sales_order_return_group_seq;

create sequence sales_order_seller_seq;

create sequence sales_order_seller_status_seq;

create sequence seller_review_seq;

create sequence seo_page_seq;

create sequence settlement_seq;

create sequence settlement_detail_seq;

create sequence shipping_city_seq;

create sequence shipping_cost_seq;

create sequence shipping_cost_detail_seq;

create sequence fashion_size_seq;

create sequence page_seq;

create sequence sub_category_banner_seq;

create sequence sub_category_banner_detail_seq;

create sequence tag_seq;

create sequence township_seq;

create sequence user_cms_seq;

create sequence user_log_seq;

create sequence vendor_seq;

create sequence village_seq;

create sequence voucher_seq;

create sequence voucher_detail_seq;

create sequence wish_list_seq;

alter table additional_category add constraint fk_additional_category_master_1 foreign key (master_id) references additional_category_master (id);
create index ix_additional_category_master_1 on additional_category (master_id);
alter table additional_category add constraint fk_additional_category_product_2 foreign key (product_id) references product (id);
create index ix_additional_category_product_2 on additional_category (product_id);
alter table additional_category add constraint fk_additional_category_userCms_3 foreign key (user_id) references user_cms (id);
create index ix_additional_category_userCms_3 on additional_category (user_id);
alter table additional_category_master add constraint fk_additional_category_master__4 foreign key (user_id) references user_cms (id);
create index ix_additional_category_master__4 on additional_category_master (user_id);
alter table address add constraint fk_address_district_5 foreign key (district_id) references district (id);
create index ix_address_district_5 on address (district_id);
alter table address add constraint fk_address_township_6 foreign key (township_id) references township (id);
create index ix_address_township_6 on address (township_id);
alter table address add constraint fk_address_region_7 foreign key (region_id) references region (id);
create index ix_address_region_7 on address (region_id);
alter table address add constraint fk_address_village_8 foreign key (village_id) references village (id);
create index ix_address_village_8 on address (village_id);
alter table address add constraint fk_address_member_9 foreign key (member_id) references member (id);
create index ix_address_member_9 on address (member_id);
alter table article add constraint fk_article_articleCategory_10 foreign key (article_category_id) references article_category (id);
create index ix_article_articleCategory_10 on article (article_category_id);
alter table article add constraint fk_article_userCms_11 foreign key (user_id) references user_cms (id);
create index ix_article_userCms_11 on article (user_id);
alter table article add constraint fk_article_changeBy_12 foreign key (change_by) references user_cms (id);
create index ix_article_changeBy_12 on article (change_by);
alter table article_category add constraint fk_article_category_userCms_13 foreign key (user_id) references user_cms (id);
create index ix_article_category_userCms_13 on article_category (user_id);
alter table article_comment add constraint fk_article_comment_replyFrom_14 foreign key (comment_parent_id) references article_comment (id);
create index ix_article_comment_replyFrom_14 on article_comment (comment_parent_id);
alter table article_comment add constraint fk_article_comment_article_15 foreign key (article_id) references article (id);
create index ix_article_comment_article_15 on article_comment (article_id);
alter table article_comment add constraint fk_article_comment_userCms_16 foreign key (approve_by) references user_cms (id);
create index ix_article_comment_userCms_16 on article_comment (approve_by);
alter table attribute add constraint fk_attribute_baseAttribute_17 foreign key (base_attribute_id) references base_attribute (id);
create index ix_attribute_baseAttribute_17 on attribute (base_attribute_id);
alter table bank add constraint fk_bank_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_bank_userCms_18 on bank (user_id);
alter table banner add constraint fk_banner_userCms_19 foreign key (user_id) references user_cms (id);
create index ix_banner_userCms_19 on banner (user_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_20 foreign key (product1_id) references product (id);
create index ix_banner_most_popular_produc_20 on banner_most_popular (product1_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_21 foreign key (product2_id) references product (id);
create index ix_banner_most_popular_produc_21 on banner_most_popular (product2_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_22 foreign key (product3_id) references product (id);
create index ix_banner_most_popular_produc_22 on banner_most_popular (product3_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_23 foreign key (product4_id) references product (id);
create index ix_banner_most_popular_produc_23 on banner_most_popular (product4_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_24 foreign key (product5_id) references product (id);
create index ix_banner_most_popular_produc_24 on banner_most_popular (product5_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_25 foreign key (product6_id) references product (id);
create index ix_banner_most_popular_produc_25 on banner_most_popular (product6_id);
alter table banner_most_popular add constraint fk_banner_most_popular_produc_26 foreign key (product7_id) references product (id);
create index ix_banner_most_popular_produc_26 on banner_most_popular (product7_id);
alter table banner_most_popular add constraint fk_banner_most_popular_userCm_27 foreign key (user_id) references user_cms (id);
create index ix_banner_most_popular_userCm_27 on banner_most_popular (user_id);
alter table blacklist_email add constraint fk_blacklist_email_userCms_28 foreign key (user_id) references user_cms (id);
create index ix_blacklist_email_userCms_28 on blacklist_email (user_id);
alter table brand add constraint fk_brand_userCms_29 foreign key (user_id) references user_cms (id);
create index ix_brand_userCms_29 on brand (user_id);
alter table catalog add constraint fk_catalog_userCms_30 foreign key (user_id) references user_cms (id);
create index ix_catalog_userCms_30 on catalog (user_id);
alter table catalog add constraint fk_catalog_parentCatalog_31 foreign key (parent_id) references catalog (id);
create index ix_catalog_parentCatalog_31 on catalog (parent_id);
alter table catalog2 add constraint fk_catalog2_lizpedia_32 foreign key (lizpedia_id) references liz_pedia (id);
create index ix_catalog2_lizpedia_32 on catalog2 (lizpedia_id);
alter table catalog2 add constraint fk_catalog2_userCms_33 foreign key (user_id) references user_cms (id);
create index ix_catalog2_userCms_33 on catalog2 (user_id);
alter table catalog_item add constraint fk_catalog_item_catalog_34 foreign key (catalog_id) references catalog2 (id);
create index ix_catalog_item_catalog_34 on catalog_item (catalog_id);
alter table catalog_item add constraint fk_catalog_item_product_35 foreign key (product_id) references product (id);
create index ix_catalog_item_product_35 on catalog_item (product_id);
alter table category add constraint fk_category_userCms_36 foreign key (user_id) references user_cms (id);
create index ix_category_userCms_36 on category (user_id);
alter table category add constraint fk_category_parentCategory_37 foreign key (parent_id) references category (id);
create index ix_category_parentCategory_37 on category (parent_id);
alter table category_banner add constraint fk_category_banner_category_38 foreign key (category_id) references category (id);
create index ix_category_banner_category_38 on category_banner (category_id);
alter table category_banner add constraint fk_category_banner_userCms_39 foreign key (user_id) references user_cms (id);
create index ix_category_banner_userCms_39 on category_banner (user_id);
alter table category_banner_detail add constraint fk_category_banner_detail_cat_40 foreign key (category_banner_id) references category_banner (id);
create index ix_category_banner_detail_cat_40 on category_banner_detail (category_banner_id);
alter table category_banner_detail add constraint fk_category_banner_detail_cat_41 foreign key (category_id) references category (id);
create index ix_category_banner_detail_cat_41 on category_banner_detail (category_id);
alter table category_banner_detail add constraint fk_category_banner_detail_sub_42 foreign key (sub_category_id) references category (id);
create index ix_category_banner_detail_sub_42 on category_banner_detail (sub_category_id);
alter table category_banner_detail add constraint fk_category_banner_detail_bra_43 foreign key (brand_id) references brand (id);
create index ix_category_banner_detail_bra_43 on category_banner_detail (brand_id);
alter table category_banner_detail add constraint fk_category_banner_detail_pro_44 foreign key (product_id) references product (id);
create index ix_category_banner_detail_pro_44 on category_banner_detail (product_id);
alter table category_banner_detail add constraint fk_category_banner_detail_use_45 foreign key (user_id) references user_cms (id);
create index ix_category_banner_detail_use_45 on category_banner_detail (user_id);
alter table category_banner_menu add constraint fk_category_banner_menu_categ_46 foreign key (category_id) references category (id);
create index ix_category_banner_menu_categ_46 on category_banner_menu (category_id);
alter table category_banner_menu add constraint fk_category_banner_menu_userC_47 foreign key (user_id) references user_cms (id);
create index ix_category_banner_menu_userC_47 on category_banner_menu (user_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_48 foreign key (category_banner_id) references category_banner_menu (id);
create index ix_category_banner_menu_detai_48 on category_banner_menu_detail (category_banner_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_49 foreign key (category_id) references category (id);
create index ix_category_banner_menu_detai_49 on category_banner_menu_detail (category_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_50 foreign key (sub_category_id) references category (id);
create index ix_category_banner_menu_detai_50 on category_banner_menu_detail (sub_category_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_51 foreign key (brand_id) references brand (id);
create index ix_category_banner_menu_detai_51 on category_banner_menu_detail (brand_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_52 foreign key (product_id) references product (id);
create index ix_category_banner_menu_detai_52 on category_banner_menu_detail (product_id);
alter table category_banner_menu_detail add constraint fk_category_banner_menu_detai_53 foreign key (user_id) references user_cms (id);
create index ix_category_banner_menu_detai_53 on category_banner_menu_detail (user_id);
alter table category_promo add constraint fk_category_promo_userCms_54 foreign key (user_id) references user_cms (id);
create index ix_category_promo_userCms_54 on category_promo (user_id);
alter table courier add constraint fk_courier_userCms_55 foreign key (user_id) references user_cms (id);
create index ix_courier_userCms_55 on courier (user_id);
alter table courier_point_location add constraint fk_courier_point_location_tow_56 foreign key (township_id) references township (id);
create index ix_courier_point_location_tow_56 on courier_point_location (township_id);
alter table courier_point_location add constraint fk_courier_point_location_cou_57 foreign key (courier_id) references courier (id);
create index ix_courier_point_location_cou_57 on courier_point_location (courier_id);
alter table courier_service add constraint fk_courier_service_courier_58 foreign key (courier_id) references courier (id);
create index ix_courier_service_courier_58 on courier_service (courier_id);
alter table district add constraint fk_district_region_59 foreign key (region_id) references region (id);
create index ix_district_region_59 on district (region_id);
alter table faq add constraint fk_faq_faqGroup_60 foreign key (faq_group_id) references information_category_group (id);
create index ix_faq_faqGroup_60 on faq (faq_group_id);
alter table faq add constraint fk_faq_userCms_61 foreign key (user_id) references user_cms (id);
create index ix_faq_userCms_61 on faq (user_id);
alter table faq_group add constraint fk_faq_group_userCms_62 foreign key (user_id) references user_cms (id);
create index ix_faq_group_userCms_62 on faq_group (user_id);
alter table footer add constraint fk_footer_staticPage_63 foreign key (static_page_id) references page (id);
create index ix_footer_staticPage_63 on footer (static_page_id);
alter table footer add constraint fk_footer_userCms_64 foreign key (user_id) references user_cms (id);
create index ix_footer_userCms_64 on footer (user_id);
alter table highlight_banner add constraint fk_highlight_banner_parentBan_65 foreign key (parent_id) references highlight_banner (id);
create index ix_highlight_banner_parentBan_65 on highlight_banner (parent_id);
alter table highlight_banner add constraint fk_highlight_banner_userCms_66 foreign key (user_id) references user_cms (id);
create index ix_highlight_banner_userCms_66 on highlight_banner (user_id);
alter table information_category_group add constraint fk_information_category_group_67 foreign key (user_id) references user_cms (id);
create index ix_information_category_group_67 on information_category_group (user_id);
alter table liz_pedia add constraint fk_liz_pedia_userCms_68 foreign key (user_id) references user_cms (id);
create index ix_liz_pedia_userCms_68 on liz_pedia (user_id);
alter table loyalty add constraint fk_loyalty_userCms_69 foreign key (user_id) references user_cms (id);
create index ix_loyalty_userCms_69 on loyalty (user_id);
alter table member_log add constraint fk_member_log_member_70 foreign key (member_id) references member (id);
create index ix_member_log_member_70 on member_log (member_id);
alter table merchant add constraint fk_merchant_district_71 foreign key (district_id) references district (id);
create index ix_merchant_district_71 on merchant (district_id);
alter table merchant add constraint fk_merchant_township_72 foreign key (township_id) references township (id);
create index ix_merchant_township_72 on merchant (township_id);
alter table merchant add constraint fk_merchant_region_73 foreign key (region_id) references region (id);
create index ix_merchant_region_73 on merchant (region_id);
alter table merchant add constraint fk_merchant_village_74 foreign key (village_id) references village (id);
create index ix_merchant_village_74 on merchant (village_id);
alter table merchant add constraint fk_merchant_courierPointLocat_75 foreign key (courier_point_location_id) references courier_point_location (id);
create index ix_merchant_courierPointLocat_75 on merchant (courier_point_location_id);
alter table merchant add constraint fk_merchant_userCms_76 foreign key (user_id) references user_cms (id);
create index ix_merchant_userCms_76 on merchant (user_id);
alter table merchant_log add constraint fk_merchant_log_merchant_77 foreign key (merchant_id) references merchant (id);
create index ix_merchant_log_merchant_77 on merchant_log (merchant_id);
alter table merchant_promo_request add constraint fk_merchant_promo_request_pro_78 foreign key (promo_id) references promo (id);
create index ix_merchant_promo_request_pro_78 on merchant_promo_request (promo_id);
alter table merchant_promo_request add constraint fk_merchant_promo_request_mer_79 foreign key (merchant_id) references merchant (id);
create index ix_merchant_promo_request_mer_79 on merchant_promo_request (merchant_id);
alter table merchant_promo_request_product add constraint fk_merchant_promo_request_pro_80 foreign key (request_id) references merchant_promo_request (id);
create index ix_merchant_promo_request_pro_80 on merchant_promo_request_product (request_id);
alter table merchant_promo_request_product add constraint fk_merchant_promo_request_pro_81 foreign key (product_id) references product (id);
create index ix_merchant_promo_request_pro_81 on merchant_promo_request_product (product_id);
alter table most_popular_banner add constraint fk_most_popular_banner_catego_82 foreign key (category_id) references category (id);
create index ix_most_popular_banner_catego_82 on most_popular_banner (category_id);
alter table most_popular_banner add constraint fk_most_popular_banner_brand_83 foreign key (brand_id) references brand (id);
create index ix_most_popular_banner_brand_83 on most_popular_banner (brand_id);
alter table most_popular_banner add constraint fk_most_popular_banner_produc_84 foreign key (product_id) references product (id);
create index ix_most_popular_banner_produc_84 on most_popular_banner (product_id);
alter table most_popular_banner add constraint fk_most_popular_banner_userCm_85 foreign key (user_id) references user_cms (id);
create index ix_most_popular_banner_userCm_85 on most_popular_banner (user_id);
alter table notification_member add constraint fk_notification_member_member_86 foreign key (member_id) references member (id);
create index ix_notification_member_member_86 on notification_member (member_id);
alter table notification_merchant add constraint fk_notification_merchant_merc_87 foreign key (merchant_id) references merchant (id);
create index ix_notification_merchant_merc_87 on notification_merchant (merchant_id);
alter table partner add constraint fk_partner_userCms_88 foreign key (user_id) references user_cms (id);
create index ix_partner_userCms_88 on partner (user_id);
alter table payment_expiration add constraint fk_payment_expiration_userCms_89 foreign key (user_id) references user_cms (id);
create index ix_payment_expiration_userCms_89 on payment_expiration (user_id);
alter table product add constraint fk_product_currency_90 foreign key (currency_cd) references currency (code);
create index ix_product_currency_90 on product (currency_cd);
alter table product add constraint fk_product_merchant_91 foreign key (merchant_id) references merchant (id);
create index ix_product_merchant_91 on product (merchant_id);
alter table product add constraint fk_product_vendor_92 foreign key (vendor_id) references vendor (id);
create index ix_product_vendor_92 on product (vendor_id);
alter table product add constraint fk_product_brand_93 foreign key (brand_id) references brand (id);
create index ix_product_brand_93 on product (brand_id);
alter table product add constraint fk_product_category_94 foreign key (category_id) references category (id);
create index ix_product_category_94 on product (category_id);
alter table product add constraint fk_product_parentCategory_95 foreign key (parent_category_id) references category (id);
create index ix_product_parentCategory_95 on product (parent_category_id);
alter table product add constraint fk_product_grandParentCategor_96 foreign key (grand_parent_category_id) references category (id);
create index ix_product_grandParentCategor_96 on product (grand_parent_category_id);
alter table product add constraint fk_product_productGroup_97 foreign key (product_group_id) references product_group (id);
create index ix_product_productGroup_97 on product (product_group_id);
alter table product add constraint fk_product_productVariantGrou_98 foreign key (product_variant_group_id) references product_variant_group (id);
create index ix_product_productVariantGrou_98 on product (product_variant_group_id);
alter table product add constraint fk_product_userCms_99 foreign key (user_id) references user_cms (id);
create index ix_product_userCms_99 on product (user_id);
alter table product add constraint fk_product_approvedBy_100 foreign key (approved_by_id) references user_cms (id);
create index ix_product_approvedBy_100 on product (approved_by_id);
alter table product_detail add constraint fk_product_detail_mainProduc_101 foreign key (product_id) references product (id);
create index ix_product_detail_mainProduc_101 on product_detail (product_id);
alter table product_detail_tmp add constraint fk_product_detail_tmp_mainPr_102 foreign key (product_id) references product_tmp (id_tmp);
create index ix_product_detail_tmp_mainPr_102 on product_detail_tmp (product_id);
alter table product_detail_variance add constraint fk_product_detail_variance_m_103 foreign key (product_id) references product (id);
create index ix_product_detail_variance_m_103 on product_detail_variance (product_id);
alter table product_detail_variance add constraint fk_product_detail_variance_c_104 foreign key (color_id) references master_color (id);
create index ix_product_detail_variance_c_104 on product_detail_variance (color_id);
alter table product_detail_variance add constraint fk_product_detail_variance_s_105 foreign key (size_id) references fashion_size (id);
create index ix_product_detail_variance_s_105 on product_detail_variance (size_id);
alter table product_group add constraint fk_product_group_lowestPrice_106 foreign key (lowest_price_product) references product (id);
create index ix_product_group_lowestPrice_106 on product_group (lowest_price_product);
alter table product_group add constraint fk_product_group_userCms_107 foreign key (user_id) references user_cms (id);
create index ix_product_group_userCms_107 on product_group (user_id);
alter table product_review add constraint fk_product_review_approvedBy_108 foreign key (approved_by_id) references user_cms (id);
create index ix_product_review_approvedBy_108 on product_review (approved_by_id);
alter table product_review add constraint fk_product_review_member_109 foreign key (member_id) references member (id);
create index ix_product_review_member_109 on product_review (member_id);
alter table product_review add constraint fk_product_review_merchant_110 foreign key (merchant_id) references merchant (id);
create index ix_product_review_merchant_110 on product_review (merchant_id);
alter table product_review add constraint fk_product_review_user_111 foreign key (user_id) references user_cms (id);
create index ix_product_review_user_111 on product_review (user_id);
alter table product_review add constraint fk_product_review_product_112 foreign key (product_id) references product (id);
create index ix_product_review_product_112 on product_review (product_id);
alter table product_stock_tmp add constraint fk_product_stock_tmp_product_113 foreign key (product_id) references product (id);
create index ix_product_stock_tmp_product_113 on product_stock_tmp (product_id);
alter table product_stock_tmp add constraint fk_product_stock_tmp_approve_114 foreign key (approved_by_id) references user_cms (id);
create index ix_product_stock_tmp_approve_114 on product_stock_tmp (approved_by_id);
alter table product_stock_tmp add constraint fk_product_stock_tmp_user_115 foreign key (user_id) references user_cms (id);
create index ix_product_stock_tmp_user_115 on product_stock_tmp (user_id);
alter table product_tmp add constraint fk_product_tmp_currency_116 foreign key (currency_cd) references currency (code);
create index ix_product_tmp_currency_116 on product_tmp (currency_cd);
alter table product_tmp add constraint fk_product_tmp_merchant_117 foreign key (merchant_id) references merchant (id);
create index ix_product_tmp_merchant_117 on product_tmp (merchant_id);
alter table product_tmp add constraint fk_product_tmp_vendor_118 foreign key (vendor_id) references vendor (id);
create index ix_product_tmp_vendor_118 on product_tmp (vendor_id);
alter table product_tmp add constraint fk_product_tmp_brand_119 foreign key (brand_id) references brand (id);
create index ix_product_tmp_brand_119 on product_tmp (brand_id);
alter table product_tmp add constraint fk_product_tmp_category_120 foreign key (category_id) references category (id);
create index ix_product_tmp_category_120 on product_tmp (category_id);
alter table product_tmp add constraint fk_product_tmp_parentCategor_121 foreign key (parent_category_id) references category (id);
create index ix_product_tmp_parentCategor_121 on product_tmp (parent_category_id);
alter table product_tmp add constraint fk_product_tmp_grandParentCa_122 foreign key (grand_parent_category_id) references category (id);
create index ix_product_tmp_grandParentCa_122 on product_tmp (grand_parent_category_id);
alter table product_tmp add constraint fk_product_tmp_productDetail_123 foreign key (detail_id) references product_detail_tmp (id_tmp);
create index ix_product_tmp_productDetail_123 on product_tmp (detail_id);
alter table product_tmp add constraint fk_product_tmp_productGroup_124 foreign key (product_group_id) references product_group (id);
create index ix_product_tmp_productGroup_124 on product_tmp (product_group_id);
alter table product_tmp add constraint fk_product_tmp_userCms_125 foreign key (user_id) references user_cms (id);
create index ix_product_tmp_userCms_125 on product_tmp (user_id);
alter table product_tmp add constraint fk_product_tmp_approvedBy_126 foreign key (approved_by_id) references user_cms (id);
create index ix_product_tmp_approvedBy_126 on product_tmp (approved_by_id);
alter table product_variant_group add constraint fk_product_variant_group_low_127 foreign key (lowest_price_product) references product (id);
create index ix_product_variant_group_low_127 on product_variant_group (lowest_price_product);
alter table product_variant_group add constraint fk_product_variant_group_use_128 foreign key (user_id) references user_cms (id);
create index ix_product_variant_group_use_128 on product_variant_group (user_id);
alter table promo add constraint fk_promo_userCms_129 foreign key (user_id) references user_cms (id);
create index ix_promo_userCms_129 on promo (user_id);
alter table purchase_order add constraint fk_purchase_order_merchant_130 foreign key (merchant_id) references merchant (id);
create index ix_purchase_order_merchant_130 on purchase_order (merchant_id);
alter table purchase_order add constraint fk_purchase_order_vendor_131 foreign key (vendor_id) references vendor (id);
create index ix_purchase_order_vendor_131 on purchase_order (vendor_id);
alter table purchase_order add constraint fk_purchase_order_userCms_132 foreign key (user_id) references user_cms (id);
create index ix_purchase_order_userCms_132 on purchase_order (user_id);
alter table purchase_order_detail add constraint fk_purchase_order_detail_po_133 foreign key (po_id) references purchase_order (id);
create index ix_purchase_order_detail_po_133 on purchase_order_detail (po_id);
alter table purchase_order_detail add constraint fk_purchase_order_detail_pro_134 foreign key (product_id) references product (id);
create index ix_purchase_order_detail_pro_134 on purchase_order_detail (product_id);
alter table purchase_order_return add constraint fk_purchase_order_return_ven_135 foreign key (vendor_id) references vendor (id);
create index ix_purchase_order_return_ven_135 on purchase_order_return (vendor_id);
alter table purchase_order_return add constraint fk_purchase_order_return_pur_136 foreign key (purchase_order_id) references purchase_order (id);
create index ix_purchase_order_return_pur_136 on purchase_order_return (purchase_order_id);
alter table purchase_order_return add constraint fk_purchase_order_return_app_137 foreign key (approved_by_id) references user_cms (id);
create index ix_purchase_order_return_app_137 on purchase_order_return (approved_by_id);
alter table purchase_order_return add constraint fk_purchase_order_return_use_138 foreign key (user_id) references user_cms (id);
create index ix_purchase_order_return_use_138 on purchase_order_return (user_id);
alter table purchase_order_return_detail add constraint fk_purchase_order_return_det_139 foreign key (purchase_order_return_id) references purchase_order_return (id);
create index ix_purchase_order_return_det_139 on purchase_order_return_detail (purchase_order_return_id);
alter table purchase_order_return_detail add constraint fk_purchase_order_return_det_140 foreign key (product_id) references product (id);
create index ix_purchase_order_return_det_140 on purchase_order_return_detail (product_id);
alter table role_feature add constraint fk_role_feature_feature_141 foreign key (feature_id) references feature (id);
create index ix_role_feature_feature_141 on role_feature (feature_id);
alter table role_feature add constraint fk_role_feature_role_142 foreign key (role_id) references role (id);
create index ix_role_feature_role_142 on role_feature (role_id);
alter table sms_blast add constraint fk_sms_blast_userCms_143 foreign key (user_id) references user_cms (id);
create index ix_sms_blast_userCms_143 on sms_blast (user_id);
alter table sms_blast add constraint fk_sms_blast_smsTemplate_144 foreign key (template_id) references sms_template (id);
create index ix_sms_blast_smsTemplate_144 on sms_blast (template_id);
alter table sales_order add constraint fk_sales_order_member_145 foreign key (member_id) references member (id);
create index ix_sales_order_member_145 on sales_order (member_id);
alter table sales_order add constraint fk_sales_order_shipmentAddre_146 foreign key (shipment_address_id) references address (id);
create index ix_sales_order_shipmentAddre_146 on sales_order (shipment_address_id);
alter table sales_order add constraint fk_sales_order_courierPointL_147 foreign key (courier_point_location_id) references courier_point_location (id);
create index ix_sales_order_courierPointL_147 on sales_order (courier_point_location_id);
alter table sales_order add constraint fk_sales_order_courier_148 foreign key (courier_id) references courier (id);
create index ix_sales_order_courier_148 on sales_order (courier_id);
alter table sales_order add constraint fk_sales_order_billingAddres_149 foreign key (billing_address_id) references address (id);
create index ix_sales_order_billingAddres_149 on sales_order (billing_address_id);
alter table sales_order add constraint fk_sales_order_bank_150 foreign key (bank_id) references bank (id);
create index ix_sales_order_bank_150 on sales_order (bank_id);
alter table sales_order add constraint fk_sales_order_approvedBy_151 foreign key (approved_by_id) references user_cms (id);
create index ix_sales_order_approvedBy_151 on sales_order (approved_by_id);
alter table sales_order add constraint fk_sales_order_userCms_152 foreign key (user_id) references user_cms (id);
create index ix_sales_order_userCms_152 on sales_order (user_id);
alter table sales_order_detail add constraint fk_sales_order_detail_produc_153 foreign key (product_id) references product (id);
create index ix_sales_order_detail_produc_153 on sales_order_detail (product_id);
alter table sales_order_detail add constraint fk_sales_order_detail_produc_154 foreign key (product_var_id) references product_detail_variance (id);
create index ix_sales_order_detail_produc_154 on sales_order_detail (product_var_id);
alter table sales_order_detail add constraint fk_sales_order_detail_mercha_155 foreign key (merchant_id) references merchant (id);
create index ix_sales_order_detail_mercha_155 on sales_order_detail (merchant_id);
alter table sales_order_detail add constraint fk_sales_order_detail_vendor_156 foreign key (vendor_id) references vendor (id);
create index ix_sales_order_detail_vendor_156 on sales_order_detail (vendor_id);
alter table sales_order_detail add constraint fk_sales_order_detail_salesO_157 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_detail_salesO_157 on sales_order_detail (sales_order_id);
alter table sales_order_detail add constraint fk_sales_order_detail_salesO_158 foreign key (sales_order_seller_id) references sales_order_seller (id);
create index ix_sales_order_detail_salesO_158 on sales_order_detail (sales_order_seller_id);
alter table sales_order_detail add constraint fk_sales_order_detail_fashio_159 foreign key (fashion_size_id) references fashion_size (id);
create index ix_sales_order_detail_fashio_159 on sales_order_detail (fashion_size_id);
alter table sales_order_payment add constraint fk_sales_order_payment_sales_160 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_payment_sales_160 on sales_order_payment (sales_order_id);
alter table sales_order_payment add constraint fk_sales_order_payment_confi_161 foreign key (confirm_by_id) references user_cms (id);
create index ix_sales_order_payment_confi_161 on sales_order_payment (confirm_by_id);
alter table sales_order_return add constraint fk_sales_order_return_salesO_162 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_return_salesO_162 on sales_order_return (sales_order_id);
alter table sales_order_return add constraint fk_sales_order_return_salesO_163 foreign key (sales_order_seller_id) references sales_order_seller (id);
create index ix_sales_order_return_salesO_163 on sales_order_return (sales_order_seller_id);
alter table sales_order_return add constraint fk_sales_order_return_salesO_164 foreign key (sales_order_return_group_id) references sales_order_return_group (id);
create index ix_sales_order_return_salesO_164 on sales_order_return (sales_order_return_group_id);
alter table sales_order_return add constraint fk_sales_order_return_member_165 foreign key (member_id) references member (id);
create index ix_sales_order_return_member_165 on sales_order_return (member_id);
alter table sales_order_return add constraint fk_sales_order_return_userCm_166 foreign key (user_id) references user_cms (id);
create index ix_sales_order_return_userCm_166 on sales_order_return (user_id);
alter table sales_order_return_detail add constraint fk_sales_order_return_detail_167 foreign key (sales_order_return_id) references sales_order_return (id);
create index ix_sales_order_return_detail_167 on sales_order_return_detail (sales_order_return_id);
alter table sales_order_return_detail add constraint fk_sales_order_return_detail_168 foreign key (product_id) references product (id);
create index ix_sales_order_return_detail_168 on sales_order_return_detail (product_id);
alter table sales_order_return_group add constraint fk_sales_order_return_group__169 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_return_group__169 on sales_order_return_group (sales_order_id);
alter table sales_order_return_group add constraint fk_sales_order_return_group__170 foreign key (member_id) references member (id);
create index ix_sales_order_return_group__170 on sales_order_return_group (member_id);
alter table sales_order_seller add constraint fk_sales_order_seller_mercha_171 foreign key (merchant_id) references merchant (id);
create index ix_sales_order_seller_mercha_171 on sales_order_seller (merchant_id);
alter table sales_order_seller add constraint fk_sales_order_seller_vendor_172 foreign key (vendor_id) references vendor (id);
create index ix_sales_order_seller_vendor_172 on sales_order_seller (vendor_id);
alter table sales_order_seller add constraint fk_sales_order_seller_salesO_173 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_seller_salesO_173 on sales_order_seller (sales_order_id);
alter table sales_order_seller add constraint fk_sales_order_seller_courie_174 foreign key (courier_id) references courier (id);
create index ix_sales_order_seller_courie_174 on sales_order_seller (courier_id);
alter table sales_order_seller add constraint fk_sales_order_seller_shippi_175 foreign key (shipping_cost_detail_id) references shipping_cost_detail (id);
create index ix_sales_order_seller_shippi_175 on sales_order_seller (shipping_cost_detail_id);
alter table sales_order_seller add constraint fk_sales_order_seller_member_176 foreign key (member_id) references member (id);
create index ix_sales_order_seller_member_176 on sales_order_seller (member_id);
alter table sales_order_seller add constraint fk_sales_order_seller_shipme_177 foreign key (shipment_address_id) references address (id);
create index ix_sales_order_seller_shipme_177 on sales_order_seller (shipment_address_id);
alter table sales_order_seller add constraint fk_sales_order_seller_courie_178 foreign key (courier_point_location_id) references courier_point_location (id);
create index ix_sales_order_seller_courie_178 on sales_order_seller (courier_point_location_id);
alter table sales_order_seller_status add constraint fk_sales_order_seller_status_179 foreign key (sales_order_seller_id) references sales_order_seller (id);
create index ix_sales_order_seller_status_179 on sales_order_seller_status (sales_order_seller_id);
alter table seller_review add constraint fk_seller_review_approvedBy_180 foreign key (approved_by_id) references user_cms (id);
create index ix_seller_review_approvedBy_180 on seller_review (approved_by_id);
alter table seller_review add constraint fk_seller_review_member_181 foreign key (member_id) references member (id);
create index ix_seller_review_member_181 on seller_review (member_id);
alter table seller_review add constraint fk_seller_review_merchant_182 foreign key (merchant_id) references merchant (id);
create index ix_seller_review_merchant_182 on seller_review (merchant_id);
alter table seller_review add constraint fk_seller_review_vendor_183 foreign key (vendor_id) references vendor (id);
create index ix_seller_review_vendor_183 on seller_review (vendor_id);
alter table seo_page add constraint fk_seo_page_userCms_184 foreign key (user_id) references user_cms (id);
create index ix_seo_page_userCms_184 on seo_page (user_id);
alter table settlement_detail add constraint fk_settlement_detail_merchan_185 foreign key (merchant_id) references merchant (id);
create index ix_settlement_detail_merchan_185 on settlement_detail (merchant_id);
alter table settlement_detail add constraint fk_settlement_detail_settlem_186 foreign key (settlement_id) references settlement (id);
create index ix_settlement_detail_settlem_186 on settlement_detail (settlement_id);
alter table shipping_city add constraint fk_shipping_city_region_187 foreign key (region_id) references region (id);
create index ix_shipping_city_region_187 on shipping_city (region_id);
alter table shipping_city add constraint fk_shipping_city_district_188 foreign key (district_id) references district (id);
create index ix_shipping_city_district_188 on shipping_city (district_id);
alter table shipping_city add constraint fk_shipping_city_township_189 foreign key (township_id) references township (id);
create index ix_shipping_city_township_189 on shipping_city (township_id);
alter table shipping_city add constraint fk_shipping_city_village_190 foreign key (village_id) references village (id);
create index ix_shipping_city_village_190 on shipping_city (village_id);
alter table shipping_city add constraint fk_shipping_city_userCms_191 foreign key (user_id) references user_cms (id);
create index ix_shipping_city_userCms_191 on shipping_city (user_id);
alter table shipping_cost add constraint fk_shipping_cost_courier_192 foreign key (courier_id) references courier (id);
create index ix_shipping_cost_courier_192 on shipping_cost (courier_id);
alter table shipping_cost add constraint fk_shipping_cost_townshipFro_193 foreign key (township_from_id) references township (id);
create index ix_shipping_cost_townshipFro_193 on shipping_cost (township_from_id);
alter table shipping_cost add constraint fk_shipping_cost_townshipTo_194 foreign key (township_to_id) references township (id);
create index ix_shipping_cost_townshipTo_194 on shipping_cost (township_to_id);
alter table shipping_cost add constraint fk_shipping_cost_userCms_195 foreign key (user_id) references user_cms (id);
create index ix_shipping_cost_userCms_195 on shipping_cost (user_id);
alter table shipping_cost_detail add constraint fk_shipping_cost_detail_ship_196 foreign key (shipping_cost_id) references shipping_cost (id);
create index ix_shipping_cost_detail_ship_196 on shipping_cost_detail (shipping_cost_id);
alter table shipping_cost_detail add constraint fk_shipping_cost_detail_serv_197 foreign key (service_id) references courier_service (id);
create index ix_shipping_cost_detail_serv_197 on shipping_cost_detail (service_id);
alter table fashion_size add constraint fk_fashion_size_userCms_198 foreign key (user_id) references user_cms (id);
create index ix_fashion_size_userCms_198 on fashion_size (user_id);
alter table page add constraint fk_page_userCms_199 foreign key (user_id) references user_cms (id);
create index ix_page_userCms_199 on page (user_id);
alter table sub_category_banner add constraint fk_sub_category_banner_categ_200 foreign key (category_id) references category (id);
create index ix_sub_category_banner_categ_200 on sub_category_banner (category_id);
alter table sub_category_banner add constraint fk_sub_category_banner_userC_201 foreign key (user_id) references user_cms (id);
create index ix_sub_category_banner_userC_201 on sub_category_banner (user_id);
alter table sub_category_banner_detail add constraint fk_sub_category_banner_detai_202 foreign key (sub_category_banner_id) references sub_category_banner (id);
create index ix_sub_category_banner_detai_202 on sub_category_banner_detail (sub_category_banner_id);
alter table sub_category_banner_detail add constraint fk_sub_category_banner_detai_203 foreign key (brand_id) references brand (id);
create index ix_sub_category_banner_detai_203 on sub_category_banner_detail (brand_id);
alter table sub_category_banner_detail add constraint fk_sub_category_banner_detai_204 foreign key (user_id) references user_cms (id);
create index ix_sub_category_banner_detai_204 on sub_category_banner_detail (user_id);
alter table township add constraint fk_township_district_205 foreign key (district_id) references district (id);
create index ix_township_district_205 on township (district_id);
alter table user_cms add constraint fk_user_cms_role_206 foreign key (role_id) references role (id);
create index ix_user_cms_role_206 on user_cms (role_id);
alter table user_log add constraint fk_user_log_user_207 foreign key (user_id) references user_cms (id);
create index ix_user_log_user_207 on user_log (user_id);
alter table vendor add constraint fk_vendor_userCms_208 foreign key (user_id) references user_cms (id);
create index ix_vendor_userCms_208 on vendor (user_id);
alter table village add constraint fk_village_township_209 foreign key (township_id) references township (id);
create index ix_village_township_209 on village (township_id);
alter table voucher add constraint fk_voucher_createdBy_210 foreign key (created_by) references user_cms (id);
create index ix_voucher_createdBy_210 on voucher (created_by);
alter table voucher add constraint fk_voucher_updatedBy_211 foreign key (updated_by) references user_cms (id);
create index ix_voucher_updatedBy_211 on voucher (updated_by);
alter table voucher add constraint fk_voucher_merchantBy_212 foreign key (merchant_by) references merchant (id);
create index ix_voucher_merchantBy_212 on voucher (merchant_by);
alter table voucher_detail add constraint fk_voucher_detail_voucher_213 foreign key (voucher_id) references voucher (id);
create index ix_voucher_detail_voucher_213 on voucher_detail (voucher_id);
alter table voucher_detail add constraint fk_voucher_detail_member_214 foreign key (member_id) references member (id);
create index ix_voucher_detail_member_214 on voucher_detail (member_id);
alter table wish_list add constraint fk_wish_list_member_215 foreign key (member_id) references member (id);
create index ix_wish_list_member_215 on wish_list (member_id);
alter table wish_list add constraint fk_wish_list_product_216 foreign key (product_id) references product (id);
create index ix_wish_list_product_216 on wish_list (product_id);



alter table banner_merchant add constraint fk_banner_merchant_banner_01 foreign key (banner_id) references banner (id);

alter table banner_merchant add constraint fk_banner_merchant_merchant_02 foreign key (merchant_id) references merchant (id);

alter table banner_category add constraint fk_banner_category_banner_01 foreign key (banner_id) references banner (id);

alter table banner_category add constraint fk_banner_category_category_02 foreign key (category_id) references category (id);

alter table banner_product add constraint fk_banner_product_banner_01 foreign key (banner_id) references banner (id);

alter table banner_product add constraint fk_banner_product_product_02 foreign key (product_id) references product (id);

alter table banner_most_popular_merchant add constraint fk_banner_most_popular_mercha_01 foreign key (banner_most_popular_id) references banner_most_popular (id);

alter table banner_most_popular_merchant add constraint fk_banner_most_popular_mercha_02 foreign key (merchant_id) references merchant (id);

alter table banner_most_popular_category add constraint fk_banner_most_popular_catego_01 foreign key (banner_most_popular_id) references banner_most_popular (id);

alter table banner_most_popular_category add constraint fk_banner_most_popular_catego_02 foreign key (category_id) references category (id);

alter table catalog_base_attribute add constraint fk_catalog_base_attribute_cat_01 foreign key (catalog_id) references catalog (id);

alter table catalog_base_attribute add constraint fk_catalog_base_attribute_bas_02 foreign key (base_attribute_id) references base_attribute (id);

alter table category_base_attribute add constraint fk_category_base_attribute_ca_01 foreign key (category_id) references category (id);

alter table category_base_attribute add constraint fk_category_base_attribute_ba_02 foreign key (base_attribute_id) references base_attribute (id);

alter table highlight_banner_merchant add constraint fk_highlight_banner_merchant__01 foreign key (highlight_banner_id) references highlight_banner (id);

alter table highlight_banner_merchant add constraint fk_highlight_banner_merchant__02 foreign key (merchant_id) references merchant (id);

alter table highlight_banner_brand add constraint fk_highlight_banner_brand_hig_01 foreign key (highlight_banner_id) references highlight_banner (id);

alter table highlight_banner_brand add constraint fk_highlight_banner_brand_bra_02 foreign key (brand_id) references brand (id);

alter table highlight_banner_category add constraint fk_highlight_banner_category__01 foreign key (highlight_banner_id) references highlight_banner (id);

alter table highlight_banner_category add constraint fk_highlight_banner_category__02 foreign key (category_id) references category (id);

alter table highlight_banner_product add constraint fk_highlight_banner_product_h_01 foreign key (highlight_banner_id) references highlight_banner (id);

alter table highlight_banner_product add constraint fk_highlight_banner_product_p_02 foreign key (product_id) references product (id);

alter table merchant_courier add constraint fk_merchant_courier_merchant_01 foreign key (merchant_id) references merchant (id);

alter table merchant_courier add constraint fk_merchant_courier_courier_02 foreign key (courier_id) references courier (id);

alter table product_base_attribute add constraint fk_product_base_attribute_pro_01 foreign key (product_id) references product (id);

alter table product_base_attribute add constraint fk_product_base_attribute_bas_02 foreign key (base_attribute_id) references base_attribute (id);

alter table product_attribute add constraint fk_product_attribute_product_01 foreign key (product_id) references product (id);

alter table product_attribute add constraint fk_product_attribute_attribut_02 foreign key (attribute_id) references attribute (id);

alter table product_fashion_size add constraint fk_product_fashion_size_produ_01 foreign key (product_id) references product (id);

alter table product_fashion_size add constraint fk_product_fashion_size_fashi_02 foreign key (fashion_size_id) references fashion_size (id);

alter table product_tmp_base_attribute add constraint fk_product_tmp_base_attribute_01 foreign key (product_tmp_id_tmp) references product_tmp (id_tmp);

alter table product_tmp_base_attribute add constraint fk_product_tmp_base_attribute_02 foreign key (base_attribute_id) references base_attribute (id);

alter table product_tmp_attribute add constraint fk_product_tmp_attribute_prod_01 foreign key (product_tmp_id_tmp) references product_tmp (id_tmp);

alter table product_tmp_attribute add constraint fk_product_tmp_attribute_attr_02 foreign key (attribute_id) references attribute (id);

alter table product_tmp_fashion_size add constraint fk_product_tmp_fashion_size_p_01 foreign key (product_tmp_id_tmp) references product_tmp (id_tmp);

alter table product_tmp_fashion_size add constraint fk_product_tmp_fashion_size_f_02 foreign key (fashion_size_id) references fashion_size (id);

alter table product_variant_group_base_attri add constraint fk_product_variant_group_base_01 foreign key (product_variant_group_id) references product_variant_group (id);

alter table product_variant_group_base_attri add constraint fk_product_variant_group_base_02 foreign key (base_attribute_id) references base_attribute (id);

alter table promo_merchant add constraint fk_promo_merchant_promo_01 foreign key (promo_id) references promo (id);

alter table promo_merchant add constraint fk_promo_merchant_merchant_02 foreign key (merchant_id) references merchant (id);

alter table promo_brand add constraint fk_promo_brand_promo_01 foreign key (promo_id) references promo (id);

alter table promo_brand add constraint fk_promo_brand_brand_02 foreign key (brand_id) references brand (id);

alter table promo_category add constraint fk_promo_category_promo_01 foreign key (promo_id) references promo (id);

alter table promo_category add constraint fk_promo_category_category_02 foreign key (category_id) references category (id);

alter table promo_product add constraint fk_promo_product_promo_01 foreign key (promo_id) references promo (id);

alter table promo_product add constraint fk_promo_product_product_02 foreign key (product_id) references product (id);

alter table sms_blast_member add constraint fk_sms_blast_member_sms_blast_01 foreign key (sms_blast_id) references sms_blast (id);

alter table sms_blast_member add constraint fk_sms_blast_member_member_02 foreign key (member_id) references member (id);

alter table sales_order_detail_voucher_detai add constraint fk_sales_order_detail_voucher_01 foreign key (sales_order_detail_id) references sales_order_detail (id);

alter table sales_order_detail_voucher_detai add constraint fk_sales_order_detail_voucher_02 foreign key (voucher_detail_id) references voucher_detail (id);

alter table sub_category_banner_detail_produ add constraint fk_sub_category_banner_detail_01 foreign key (sub_category_banner_detail_id) references sub_category_banner_detail (id);

alter table sub_category_banner_detail_produ add constraint fk_sub_category_banner_detail_02 foreign key (product_id) references product (id);

alter table tag_article add constraint fk_tag_article_tag_01 foreign key (tag_id) references tag (id);

alter table tag_article add constraint fk_tag_article_article_02 foreign key (article_id) references article (id);

alter table tag_product add constraint fk_tag_product_tag_01 foreign key (tag_id) references tag (id);

alter table tag_product add constraint fk_tag_product_product_02 foreign key (product_id) references product (id);

alter table voucher_merchant add constraint fk_voucher_merchant_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_merchant add constraint fk_voucher_merchant_merchant_02 foreign key (merchant_id) references merchant (id);

alter table voucher_brand add constraint fk_voucher_brand_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_brand add constraint fk_voucher_brand_brand_02 foreign key (brand_id) references brand (id);

alter table voucher_category add constraint fk_voucher_category_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_category add constraint fk_voucher_category_category_02 foreign key (category_id) references category (id);

alter table voucher_product add constraint fk_voucher_product_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_product add constraint fk_voucher_product_product_02 foreign key (product_id) references product (id);

alter table voucher_member add constraint fk_voucher_member_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_member add constraint fk_voucher_member_member_02 foreign key (member_id) references member (id);

# --- !Downs

drop table if exists additional_category cascade;

drop table if exists additional_category_master cascade;

drop table if exists address cascade;

drop table if exists article cascade;

drop table if exists tag_article cascade;

drop table if exists article_category cascade;

drop table if exists article_comment cascade;

drop table if exists attribute cascade;

drop table if exists bank cascade;

drop table if exists banner cascade;

drop table if exists banner_merchant cascade;

drop table if exists banner_category cascade;

drop table if exists banner_product cascade;

drop table if exists banner_most_popular cascade;

drop table if exists banner_most_popular_merchant cascade;

drop table if exists banner_most_popular_category cascade;

drop table if exists base_attribute cascade;

drop table if exists blacklist_email cascade;

drop table if exists brand cascade;

drop table if exists catalog cascade;

drop table if exists catalog_base_attribute cascade;

drop table if exists catalog2 cascade;

drop table if exists catalog_item cascade;

drop table if exists category cascade;

drop table if exists category_base_attribute cascade;

drop table if exists category_banner cascade;

drop table if exists category_banner_detail cascade;

drop table if exists category_banner_menu cascade;

drop table if exists category_banner_menu_detail cascade;

drop table if exists category_promo cascade;

drop table if exists change_log cascade;

drop table if exists config_settings cascade;

drop table if exists courier cascade;

drop table if exists courier_point_location cascade;

drop table if exists courier_service cascade;

drop table if exists currency cascade;

drop table if exists currency_exchange_rate cascade;

drop table if exists district cascade;

drop table if exists faq cascade;

drop table if exists faq_group cascade;

drop table if exists feature cascade;

drop table if exists footer cascade;

drop table if exists highlight_banner cascade;

drop table if exists highlight_banner_merchant cascade;

drop table if exists highlight_banner_brand cascade;

drop table if exists highlight_banner_category cascade;

drop table if exists highlight_banner_product cascade;

drop table if exists images cascade;

drop table if exists information_category_group cascade;

drop table if exists liz_pedia cascade;

drop table if exists loyalty cascade;

drop table if exists master_color cascade;

drop table if exists member cascade;

drop table if exists member_log cascade;

drop table if exists merchant cascade;

drop table if exists merchant_courier cascade;

drop table if exists merchant_log cascade;

drop table if exists merchant_promo_request cascade;

drop table if exists merchant_promo_request_product cascade;

drop table if exists mobile_version cascade;

drop table if exists most_popular_banner cascade;

drop table if exists notification_member cascade;

drop table if exists notification_merchant cascade;

drop table if exists param cascade;

drop table if exists partner cascade;

drop table if exists payment_expiration cascade;

drop table if exists photo cascade;

drop table if exists product cascade;

drop table if exists product_base_attribute cascade;

drop table if exists product_attribute cascade;

drop table if exists product_fashion_size cascade;

drop table if exists tag_product cascade;

drop table if exists product_detail cascade;

drop table if exists product_detail_tmp cascade;

drop table if exists product_detail_variance cascade;

drop table if exists product_group cascade;

drop table if exists product_review cascade;

drop table if exists product_stock_tmp cascade;

drop table if exists product_tmp cascade;

drop table if exists product_tmp_base_attribute cascade;

drop table if exists product_tmp_attribute cascade;

drop table if exists product_tmp_fashion_size cascade;

drop table if exists product_variant_group cascade;

drop table if exists product_variant_group_base_attri cascade;

drop table if exists promo cascade;

drop table if exists promo_merchant cascade;

drop table if exists promo_brand cascade;

drop table if exists promo_category cascade;

drop table if exists promo_product cascade;

drop table if exists purchase_order cascade;

drop table if exists purchase_order_detail cascade;

drop table if exists purchase_order_return cascade;

drop table if exists purchase_order_return_detail cascade;

drop table if exists region cascade;

drop table if exists role cascade;

drop table if exists role_feature cascade;

drop table if exists sms_blast cascade;

drop table if exists sms_blast_member cascade;

drop table if exists sms_template cascade;

drop table if exists sales_order cascade;

drop table if exists sales_order_detail cascade;

drop table if exists sales_order_detail_voucher_detai cascade;

drop table if exists sales_order_payment cascade;

drop table if exists sales_order_return cascade;

drop table if exists sales_order_return_detail cascade;

drop table if exists sales_order_return_group cascade;

drop table if exists sales_order_seller cascade;

drop table if exists sales_order_seller_status cascade;

drop table if exists seller_review cascade;

drop table if exists seo_page cascade;

drop table if exists settlement cascade;

drop table if exists settlement_detail cascade;

drop table if exists shipping_city cascade;

drop table if exists shipping_cost cascade;

drop table if exists shipping_cost_detail cascade;

drop table if exists fashion_size cascade;

drop table if exists page cascade;

drop table if exists sub_category_banner cascade;

drop table if exists sub_category_banner_detail cascade;

drop table if exists sub_category_banner_detail_produ cascade;

drop table if exists tag cascade;

drop table if exists township cascade;

drop table if exists user_cms cascade;

drop table if exists user_log cascade;

drop table if exists vendor cascade;

drop table if exists village cascade;

drop table if exists voucher cascade;

drop table if exists voucher_merchant cascade;

drop table if exists voucher_brand cascade;

drop table if exists voucher_category cascade;

drop table if exists voucher_product cascade;

drop table if exists voucher_member cascade;

drop table if exists voucher_detail cascade;

drop table if exists wish_list cascade;

drop sequence if exists additional_category_seq;

drop sequence if exists additional_category_master_seq;

drop sequence if exists address_seq;

drop sequence if exists article_seq;

drop sequence if exists article_category_seq;

drop sequence if exists article_comment_seq;

drop sequence if exists attribute_seq;

drop sequence if exists bank_seq;

drop sequence if exists banner_seq;

drop sequence if exists banner_most_popular_seq;

drop sequence if exists base_attribute_seq;

drop sequence if exists blacklist_email_seq;

drop sequence if exists brand_seq;

drop sequence if exists catalog_seq;

drop sequence if exists catalog2_seq;

drop sequence if exists catalog_item_seq;

drop sequence if exists category_seq;

drop sequence if exists category_banner_seq;

drop sequence if exists category_banner_detail_seq;

drop sequence if exists category_banner_menu_seq;

drop sequence if exists category_banner_menu_detail_seq;

drop sequence if exists category_promo_seq;

drop sequence if exists config_settings_seq;

drop sequence if exists courier_seq;

drop sequence if exists courier_point_location_seq;

drop sequence if exists courier_service_seq;

drop sequence if exists currency_seq;

drop sequence if exists district_seq;

drop sequence if exists faq_seq;

drop sequence if exists faq_group_seq;

drop sequence if exists feature_seq;

drop sequence if exists footer_seq;

drop sequence if exists highlight_banner_seq;

drop sequence if exists images_seq;

drop sequence if exists information_category_group_seq;

drop sequence if exists liz_pedia_seq;

drop sequence if exists loyalty_seq;

drop sequence if exists master_color_seq;

drop sequence if exists member_seq;

drop sequence if exists member_log_seq;

drop sequence if exists merchant_seq;

drop sequence if exists merchant_log_seq;

drop sequence if exists merchant_promo_request_seq;

drop sequence if exists merchant_promo_request_product_seq;

drop sequence if exists mobile_version_seq;

drop sequence if exists most_popular_banner_seq;

drop sequence if exists notification_member_seq;

drop sequence if exists notification_merchant_seq;

drop sequence if exists partner_seq;

drop sequence if exists payment_expiration_seq;

drop sequence if exists photo_seq;

drop sequence if exists product_seq;

drop sequence if exists product_detail_seq;

drop sequence if exists product_detail_tmp_seq;

drop sequence if exists product_detail_variance_seq;

drop sequence if exists product_group_seq;

drop sequence if exists product_review_seq;

drop sequence if exists product_stock_tmp_seq;

drop sequence if exists product_tmp_seq;

drop sequence if exists product_variant_group_seq;

drop sequence if exists promo_seq;

drop sequence if exists purchase_order_seq;

drop sequence if exists purchase_order_detail_seq;

drop sequence if exists purchase_order_return_seq;

drop sequence if exists purchase_order_return_detail_seq;

drop sequence if exists region_seq;

drop sequence if exists role_seq;

drop sequence if exists sms_blast_seq;

drop sequence if exists sms_template_seq;

drop sequence if exists sales_order_seq;

drop sequence if exists sales_order_detail_seq;

drop sequence if exists sales_order_payment_seq;

drop sequence if exists sales_order_return_seq;

drop sequence if exists sales_order_return_detail_seq;

drop sequence if exists sales_order_return_group_seq;

drop sequence if exists sales_order_seller_seq;

drop sequence if exists sales_order_seller_status_seq;

drop sequence if exists seller_review_seq;

drop sequence if exists seo_page_seq;

drop sequence if exists settlement_seq;

drop sequence if exists settlement_detail_seq;

drop sequence if exists shipping_city_seq;

drop sequence if exists shipping_cost_seq;

drop sequence if exists shipping_cost_detail_seq;

drop sequence if exists fashion_size_seq;

drop sequence if exists page_seq;

drop sequence if exists sub_category_banner_seq;

drop sequence if exists sub_category_banner_detail_seq;

drop sequence if exists tag_seq;

drop sequence if exists township_seq;

drop sequence if exists user_cms_seq;

drop sequence if exists user_log_seq;

drop sequence if exists vendor_seq;

drop sequence if exists village_seq;

drop sequence if exists voucher_seq;

drop sequence if exists voucher_detail_seq;

drop sequence if exists wish_list_seq;

