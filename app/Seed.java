import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;
import models.*;
import models.Currency;

import java.text.SimpleDateFormat;
import java.util.*;

public class Seed {

//    public static void seedTestingRoleFeatureUser() {
//        if (Feature.find.findRowCount() == 0 && Role.find.findRowCount() == 0 && UserCms.find.findRowCount() == 0) {
//            Feature bannerF = new Feature("Banner", "Information", "Can view, create, and edit banner data.", true);
//            bannerF.save();
//            Feature faqF = new Feature("FAQ", "Information", "Can view, create, and edit FAQ data.", true);
//            faqF.save();
//            Feature pageF = new Feature("StaticPage", "Information", "Can view, create, and edit static page data.",
//                    true);
//            pageF.save();
//            Feature articleF = new Feature("Article", "Information", "Can view, create, and edit article data.", true);
//            articleF.save();
//            Feature subscribeF = new Feature("Subscriber", "Newsletter", "Can view and edit subscriber data.", true);
//            subscribeF.save();
//            Feature NlDesignF = new Feature("Newsletter Design", "Newsletter",
//                    "Can view, create, and edit newsletter's design data.", true);
//            NlDesignF.save();
//            Feature NlSendF = new Feature("Newsletter Send", "Newsletter",
//                    "Can view, create, and edit newsletter data.", true);
//            NlSendF.save();
//            Feature addMerchF = new Feature("Merchant", "Merchant Management",
//                    "Can view, create, and edit merchant data.", true);
//            addMerchF.save();
//            Feature merchLocF = new Feature("Merchant's location", "Merchant Management",
//                    "Can view, create, and edit merchant's location data.", true);
//            merchLocF.save();
//            Feature categoryF = new Feature("Category", "Product", "Can view, create, and edit category data.", true);
//            categoryF.save();
//            Feature brandF = new Feature("Brand", "Product", "Can view, create, and edit brand data.", true);
//            brandF.save();
//            Feature prodListF = new Feature("Product List", "Product", "Can view, create, and edit product data.",
//                    true);
//            prodListF.save();
//            Feature topSalesF = new Feature("Top Sales", "Product", "Can view top sales data.", true);
//            topSalesF.save();
//            Feature orderF = new Feature("Order", "Shops", "Can view status and detail of an order.", true);
//            orderF.save();
//            Feature returnF = new Feature("Order Return", "Shops", "Can view and create returned order data.", true);
//            returnF.save();
//            Feature shippingF = new Feature("Shipping", "Shops", "Can view and edit detail of order's delivery.", true);
//            shippingF.save();
//            Feature ship3plF = new Feature("Shipping 3PL", "Shops",
//                    "Can view, create, and edit detail of order's delivery that using third party logistic.", true);
//            ship3plF.save();
//            Feature voucherF = new Feature("List Voucher", "Voucher", "Can view, create, and edit custom voucher.",
//                    true);
//            voucherF.save();
//            Feature promotionF = new Feature("Promotion", "Voucher", "Can view, create, and edit promotion data.",
//                    true);
//            promotionF.save();
//            Feature roleF = new Feature("Role", "User Management", "Can view, create and edit user's role data.", true);
//            roleF.save();
//            Feature userF = new Feature("User", "User Management", "Can view, create and edit cms user data.", true);
//            userF.save();
//            Feature dashboardF = new Feature("Admin dashboard", "",
//                    "Can view graphic about new users and visitors with selected filters.", true);
//            dashboardF.save();
//            Feature customerF = new Feature("Customer", "", "Can view and edit registered customer data.", true);
//            customerF.save();
//            Feature profileF = new Feature("Profile", "", "Can view and edit personal information.", true);
//            profileF.save();
//            Feature reportF = new Feature("Report", "",
//                    "Can create report about order and shipping with selected filters", true);
//            reportF.save();
//            Feature logsF = new Feature("Logs", "", "Can view activity history that happen in system", true);
//            logsF.save();
//            Feature themF = new Feature("Thematics", "Product", "Can view and edit thematic data", true);
//            themF.save();
//            Feature wishF = new Feature("Wishlist", "Product", "Can view customer's wishlist data", true);
//            wishF.save();
//
//            // role
//            Role adminR = new Role("Admin", "Administrator", true);
//            Role sellerR = new Role("Seller", "Product Seller", true);
//            Role snMarketR = new Role("Sales and Marketting", "Sales and Marketing report", true);
//            Role datEntryR = new Role("Data Entry", "Input market's data", true);
//            Role contentR = new Role("Content", "Manage static content", true);
//            // assign feature to role
////            adminR.setFeature(bannerF, faqF, pageF, articleF, subscribeF, NlDesignF, NlSendF, addMerchF, merchLocF,
////                    categoryF, brandF, prodListF, topSalesF, orderF, returnF, shippingF, ship3plF, voucherF, promotionF,
////                    roleF, userF, dashboardF, customerF, profileF, reportF, logsF, themF, wishF);
////            adminR.save();
//            // TODO +ProductS?
////            sellerR.setFeature(prodListF, categoryF, brandF, addMerchF, merchLocF, profileF);
////            sellerR.save();
////            snMarketR.setFeature(subscribeF, NlDesignF, NlSendF, topSalesF, orderF, returnF, shippingF, ship3plF,
////                    reportF, profileF, wishF);
////            snMarketR.save();
////            datEntryR.setFeature(categoryF, brandF, prodListF, voucherF, promotionF, profileF, themF);
////            datEntryR.save();
////            contentR.setFeature(bannerF, faqF, pageF, articleF, profileF);
////            contentR.save();
//
//            // user
//            try {
//                UserCms admin = new UserCms("password", "admin", "", "admin@hokeba.com", "", "M",
//                        "1945-8-17");
//                admin.role = adminR;
//                admin.save();
////                UserCms seller = new UserCms("password", "seller", "", "seller@hokeba.com", "", "M",
////                        "1945-8-17");
////                seller.role = sellerR;
////                seller.save();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public static void seedConfigSettings() {
//        if (ConfigSettings.find.findRowCount() == 0) {
//            ConfigSettings set1 = new ConfigSettings();
//            set1.module = "footer";
//            set1.name = "position";
//            set1.key = "footer_pos";
//            set1.value = "[\"Customer Care\",\"About Us\"]";
//            set1.save();
//            ConfigSettings set2 = new ConfigSettings();
//            set2.module = "article";
//            set2.name = "status";
//            set2.key = "article_sta";
//            set2.value = "[\"draft\",\"publish\",\"non-active\"]";
//            set2.save();
//        }
//    }

    public static void seedGroup() {
        if (InformationCategoryGroup.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);

            Transaction txn = Ebean.beginTransaction();

            try {
                InformationCategoryGroup icg1 = InformationCategoryGroup.seed("Sign Up & Register", "faq", user);
                InformationCategoryGroup icg2 = InformationCategoryGroup.seed("Buyers guide", "faq", user);
                InformationCategoryGroup icg3 = InformationCategoryGroup.seed("FAQ - Most Frequently Asked Questions", "faq", user);
                InformationCategoryGroup icg4 = InformationCategoryGroup.seed("Review Order", "faq", user);
                InformationCategoryGroup icg5 = InformationCategoryGroup.seed("Security", "faq", user);
                InformationCategoryGroup icg6 = InformationCategoryGroup.seed("Contact Hokeba", "faq", user);

                Faq.seed(1, "Sign Up with Email List", icg1, user);
                Faq.seed(2, "Sign Up with Facebook", icg1, user);
                Faq.seed(3, "Log In with Email", icg1, user);
                Faq.seed(4, "Log In with Facebook Account", icg1, user);
                Faq.seed(5, "Activation Method", icg1, user);

                Faq.seed(1, "How to Shop", icg2, user);
                Faq.seed(2, "How to Add a Note or the Product Description", icg2, user);
                Faq.seed(3, "How to Download & Invoice Receipt", icg2, user);
                Faq.seed(4, "How to Use Voucher Code in Hokeba.com", icg2, user);
                Faq.seed(5, "If the Transaction Cancelled", icg2, user);

                Faq.seed(1, "What was unique code and transfer news?", icg3, user);
                Faq.seed(2, "Why was my payment has not been verified?", icg3, user);
                Faq.seed(3, "What if the payment is less or more?", icg3, user);
                Faq.seed(4, "I confirmed the payment, how they verified?", icg3, user);
                Faq.seed(5, "What if the goods received are not suitable / less?", icg3, user);

                Faq.seed(1, "How to provide value and product reviews", icg4, user);
                Faq.seed(1, "Tips on keeping your account secure", icg5, user);
                Faq.seed(1, "Contact Hokeba Support", icg6, user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }


        }
    }

    // member dummy data
    public static void seedTestingMember() {
        if (Member.find.findRowCount() == 0) {
            District d1 = District.find.byId(1l);
            District d2 = District.find.byId(6l);
            District d3 = District.find.byId(11l);

            Township t1 = Township.find.where().eq("district", d1).setMaxRows(1).findUnique();
            Township t2 = Township.find.where().eq("district", d2).setMaxRows(1).findUnique();
            Township t3 = Township.find.where().eq("district", d3).setMaxRows(1).findUnique();

            List<Address> a1 = new ArrayList<>();
            a1.add(new Address(true, "hokeba user 1","Jl Suharti", d1, t1, 1, "12345", "08123432123"));
            a1.add(new Address(true, "hokeba user 1", "Jl Suharti", d1, t1, 2, "12345", "08123432123"));
            List<Address> a2 = new ArrayList<>();
            a2.add(new Address(true, "hokeba user 2","Jl Mergosari", d1, t1, 1, "12345", "08123432123"));
            a2.add(new Address(true, "hokeba user 2", "Jl Mergosari", d1, t1, 2, "12345", "08123432123"));
            List<Address> a3 = new ArrayList<>();
            a3.add(new Address(true, "hokeba x user 1", "Jl Taman Kopo Indah", d2, t2, 1, "12345", "08123432123"));
            a3.add(new Address(true, "hokeba x user 1", "Jl Taman Kopo Indah", d2, t2, 2, "12345", "08123432123"));
            List<Address> a4 = new ArrayList<>();
            a4.add(new Address(true, "hokeba x user 2", "Jl Cimindi Raya", d2, t3, 1, "12345", "08123432123"));
            a4.add(new Address(true, "hokeba x user 2", "Jl Cimindi Raya", d2, t3, 2, "12345", "08123432123"));
            List<Address> a5 = new ArrayList<>();
            a5.add(new Address(true, "hokeba x user 3", "Jl Kembar Mas", d3, t3, 1, "12345", "08123432123"));
            a5.add(new Address(true, "hokeba x user 3", "Jl Kembar Mas", d3, t3, 2, "12345", "08123432123"));


            List<Member> members = new ArrayList<Member>();
            try {
                members.add(new Member("password1", "hokeba user 1", "mailinator", "hokebauser1@mailinator.com",
                        "081809470001", "M", "1945-8-17", "", a1, true, true, 28));
                members.add(new Member("password1", "hokeba user 2", "mailinator", "hokebauser2@mailinator.com",
                        "081809470002", "M", "1945-8-17", "", a2, true, true, 29));
                members.add(new Member("password1", "hokeba x user 1", "", "hokebaxuser1@mailinator.com",
                        "081809470003", "M", "1945-8-17", "", a3, true, true, 30));
                members.add(new Member("password1", "hokeba x user 2", "", "hokebaxuser2@mailinator.com",
                        "081809470004", "M", "1945-8-17", "", a4, true, true, 31));
                members.add(new Member("password1", "hokeba x user 3", "", "hokebaxuser3@mailinator.com",
                        "081809470005", "M", "1945-8-17", "", a5, true, true, 32));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (Member m : members) {
                    m.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // product dummy data
    public static void seedTestingProduct() {
        if (Product.find.findRowCount() == 0) {
            // TODO currency
//            Currency cur1 = new Currency("USD", "US $", "US Dollar", 2);
//            Currency cur2 = new Currency("IDR", "RP", "Indonesian Rupiah", 1);
            Currency cur3 = new Currency("MMK", "MMK", "Myanmar Kyat", 3);
//            cur1.save();
//            cur2.save();
            cur3.save();

            BaseAttribute ba1 = BaseAttribute.find.byId(1L);
            BaseAttribute ba2 = BaseAttribute.find.byId(2L);
            BaseAttribute ba3 = BaseAttribute.find.byId(3L);
            BaseAttribute ba4 = BaseAttribute.find.byId(4L);

            Set<BaseAttribute> c1 = new HashSet<>(Arrays.asList(ba1, ba4, ba3, ba2));
            Set<BaseAttribute> c2 = new HashSet<>(Arrays.asList(ba1, ba2));
            Set<BaseAttribute> c3 = new HashSet<>(Arrays.asList(ba1, ba4, ba2));
            Set<BaseAttribute> c4 = new HashSet<>(Arrays.asList(ba1, ba2));

            Attribute at1 = Attribute.find.where().eq("baseAttribute", ba1).setMaxRows(1).findUnique();
            Attribute at2 = Attribute.find.where().eq("baseAttribute", ba2).setMaxRows(1).findUnique();
            Attribute at3 = Attribute.find.where().eq("baseAttribute", ba3).setMaxRows(1).findUnique();
            Attribute at4 = Attribute.find.where().eq("baseAttribute", ba4).setMaxRows(1).findUnique();

            Set<Attribute> a1 = new HashSet<>(Arrays.asList(at1, at4, at3, at2));
            Set<Attribute> a2 = new HashSet<>(Arrays.asList(at1, at2));
            Set<Attribute> a3 = new HashSet<>(Arrays.asList(at1, at4, at2));
            Set<Attribute> a4 = new HashSet<>(Arrays.asList(at1, at2));


            // TODO main prod
            ProductGroup productGroup1 = ProductGroup.find.byId(new Long(1));
            ProductGroup productGroup2 = ProductGroup.find.byId(new Long(2));
            ProductGroup productGroup3 = ProductGroup.find.byId(new Long(3));
            ProductGroup productGroup4 = ProductGroup.find.byId(new Long(4));
            Product.seed("rpc1", 3, "LG V20", 6L, 11L, 1L, "product/131216-111626_lg_v20.jpg", productGroup2, 2, c1, a1);
            Product.seed("rpc2", 3, "HP Deskjet Ink Advantage 2135 All In One Printer", 6L, 17L, 1L, "product/printer.jpg", productGroup4, 3, c2, a2);
            Product.seed("rpc3", 3, "LG 32 LED Digital HD TV - Hitam (Model 32LH510D)", 6L, 26L, 1L, "product/lg-tv.jpg", productGroup1, 4, c4, a4);
            Product.seed("rpc4", 3, "DJI Mavic Pro Collapsible Travelling Quadcopter Drone", 6L, 22L, 1L, "product/drone.jpg", null, 5, c3, a3);
            Product.seed("rpc5", 3, "Delcell Power Bank ECO Polymer Battery Real Capacity - 10000 mAh", 6L, 9L, 1L, "product/power-bank.jpg", null, 6, c1, a1);
            Product.seed("rpc6", 3, "Case TPU Phone Case for Xiaomi Redmi Note 4 - Black", 6L, 10L, 1L, "product/phone-case.jpg", null, 7, c1, a1);
            Product.seed("rpc7", 3, "Tempered Glass Vikento Samsung Galaxy Ace 3 / S7270 - Premium Tempered Glass - Screen Protector", 6L, 12L, 1L, "product/screen-protector.jpg", null, 8, c1, a1);
            Product.seed("rpc8", 1, "Car Charger 4 in 1 Dual 3.1A USB Voltage Adapter for iPhone Samsung 651967", 6L, 13L, 1L, "product/charger.jpg", null, 9, c1, a1);
            Product.seed("rpc9", 1, "WD My Passport ULTRA New Design 1TB Portable Storage USB 3.0", 6L, 14L, 1L, "product/harddisk.jpg", null, 10, c2, a2);
            Product.seed("rpc10", 1, "Allwin HDMI to RGB Component (YPbPr) Video +R/L Audio Adapter Converter HD TV", 6L, 15L, 1L, "product/computer-component.jpg", null, 11, c2, a2);
            Product.seed("rpc11", 1, "Backlight Keyboard Mouse Combos Special Computer Accesories Gaming Gamer Laser USB 3.0 with Teclado GamerBlack", 6L, 16L, 1L, "product/keyboard.jpg", productGroup4, 12, c2, a2);
            Product.seed("rpc12", 1, "4K Action Sports Camera Full HD WiFi DV Cam 170 Degree Wide Lens - Black", 6L, 18L, 1L, "product/action-camera.jpg", null, 13, c3, a3);
            Product.seed("rpc13", 1, "SHOOT - 3 Way Foldable Extension Tripod Monopod Selfie Stick for GoPro Hero 5/4/3/2/1 SJCAM SJ6000/5000/4000 Xiaomi Yi Camera", 6L, 19L, 1L, "product/point-shoot-camera.jpg", null, 14, c3, a3);
            Product.seed("rpc14", 1, "Nikon D5200 Lensa Kit 18-55mm NON VR - 24.1 MP", 6L, 20L, 1L, "product/dslr-camera.jpg", productGroup3, 15, c3, a3);
            Product.seed("rpc15", 1, "Fujifilm X-A3 Mirrorless Camera with XC 16-50mm Lens - 24.2MP - Compatible with Fujifilm App", 6L, 21L, 2L, "product/mirrorless-camera.jpg", productGroup3, 16, c3, a3);
            Product.seed("rpc16", 1, "Mulba Crystal Instant Camera Case For Fujifilm Instax Mini 8 Transparent Pink", 6L, 23L, 2L, "product/instant-camera.jpg", productGroup3, 17, c3, a3);
            Product.seed("rpc17", 1, "Toshiba - 49\" Class (48.5\" Diag.) - LED - 2160p - with Chromecast Built-in - 4K Ultra HD TV - Black", 6L, 24L, 2L, "product/4k-tvs.jpg", productGroup1, 18, c4, a4);
            Product.seed("rpc18", 3, "Samsung 50 Inch UHD 4K Flat Smart LED Digital TV 50KU6000", 6L, 25L, 1L, "product/smart-tvs.jpg", productGroup1, 19, c4, a4);
            Product.seed("rpc19", 3, "LG 42\" - LCD 3D TV - Hitam - 42LA6130", 6L, 27L, 1L, "product/lcd-tvs.jpg",productGroup1, 20, c4, a4);
            productGroup1.lowestPriceProduct = Product.find.byId(new Long(18));
            productGroup2.lowestPriceProduct = Product.find.byId(new Long(1));
            productGroup3.lowestPriceProduct = Product.find.byId(new Long(15));
            productGroup4.lowestPriceProduct = Product.find.byId(new Long(11));

            productGroup1.update();
            productGroup2.update();
            productGroup3.update();
            productGroup4.update();
        }
    }

    public static void seedTestingAttribute() {
        if (BaseAttribute.find.findRowCount() == 0) {

            // TODO base att
            BaseAttribute ba1 = new BaseAttribute("Color", "varchar", 1);
            BaseAttribute ba2 = new BaseAttribute("Screen Size (inches)", "integer", 2);
            BaseAttribute ba3 = new BaseAttribute("Operating System", "varchar", 3);
            BaseAttribute ba4 = new BaseAttribute("Storage Capacity", "varchar", 4);
            ba1.save();
            ba2.save();
            ba3.save();
            ba4.save();

            // TODO att data
            Attribute ad11 = new Attribute("RED", ba1, 3);
            Attribute ad12 = new Attribute("BLUE", ba1, 4);
            Attribute ad13 = new Attribute("GREEN", ba1, 5);
            Attribute ad14 = new Attribute("WHITE", ba1, 1);
            Attribute ad15 = new Attribute("BLACK", ba1, 2);
            Attribute ad21 = new Attribute("3.5", ba2, 6);
            Attribute ad22 = new Attribute("4", ba2, 7);
            Attribute ad23 = new Attribute("4.7", ba2, 8);
            Attribute ad24 = new Attribute("5.5", ba2, 9);
            Attribute ad25 = new Attribute("7", ba2, 10);
            Attribute ad31 = new Attribute("Android", ba3, 11);
            Attribute ad32 = new Attribute("BlackBerry", ba3, 12);
            Attribute ad33 = new Attribute("iPhone", ba3, 13);
            Attribute ad34 = new Attribute("Windows Mobile", ba3, 14);
            Attribute ad41 = new Attribute("2GB", ba4, 15);
            Attribute ad42 = new Attribute("4GB", ba4, 16);
            Attribute ad43 = new Attribute("8GB", ba4, 17);
            Attribute ad44 = new Attribute("16GB", ba4, 18);
            Attribute ad45 = new Attribute("32GB", ba4, 19);
            Attribute ad46 = new Attribute("64GB", ba4, 20);
            Attribute ad47 = new Attribute("128GB", ba4, 21);

            ad14.save();
            ad15.save();
            ad11.save();
            ad12.save();
            ad13.save();
            ad21.save();
            ad22.save();
            ad23.save();
            ad24.save();
            ad25.save();
            ad31.save();
            ad32.save();
            ad33.save();
            ad34.save();
            ad41.save();
            ad42.save();
            ad43.save();
            ad44.save();
            ad45.save();
            ad46.save();
            ad47.save();
        }
    }

    // category dummy data
    public static void seedTestingCategory() {
        if (Category.find.findRowCount() == 0) {
            Category cat1 = Category.seed("Mobiles & Tablets", "cat_020317-145457_mobiles-tablets.jpg", "banner/icon-cat-1.png", 1L, 0L, 3);
            Category cat2 = Category.seed("Computers & Laptops", "cat_020317-145555_computers-laptops.jpg", "banner/icon-cat-2.png", 1L, 0L, 4);
            Category cat3 = Category.seed("Cameras", "cat_020317-145651_cameras.jpg", "banner/icon-cat-4.png", 1L, 0L, 5);
            Category cat4 = Category.seed("TV & Gaming", "cat_020317-145742_tv-gaming.jpg", "banner/icon-cat-3.png", 1L, 0L, 6);

            Category cat11 = Category.seed("Mobiles And Tablets", "cat_020317-152635_mobiles-and-tablets.jpg", "",1L, cat1.id, 7);
            Category cat21 = Category.seed("Desktop", "cat_020317-153204_desktop.jpg", "", 1L, cat2.id, 8);
            Category cat31 = Category.seed("Camera", "cat_020317-153234_camera.jpg", "", 1L, cat3.id, 9);
            Category cat41 = Category.seed("TV", "cat_020317-153302_tv.jpg", "", 1L, cat4.id, 10);

            BaseAttribute ba1 = BaseAttribute.find.byId(1L);
            BaseAttribute ba2 = BaseAttribute.find.byId(2L);
            BaseAttribute ba3 = BaseAttribute.find.byId(3L);
            BaseAttribute ba4 = BaseAttribute.find.byId(4L);

            List<BaseAttribute> c1 = Arrays.asList(ba1, ba4, ba3, ba2);
            List<BaseAttribute> c2 = Arrays.asList(ba1, ba2);
            List<BaseAttribute> c3 = Arrays.asList(ba1, ba2, ba4);
            List<BaseAttribute> c4 = Arrays.asList(ba1, ba2);

            Category.seed("Power Banks", "default/ic_power_bank.png", "default/ic_power_bank.png", 1L, cat11.id, 11, c1, 3D);
            Category.seed("Phone Cases", "default/ic_handphone.png", "default/ic_handphone.png", 1L, cat11.id, 12, c1, 3D);
            Category.seed("Tablet Accessories", "default/ic_tablets.png", "default/ic_tablets.png", 1L, cat11.id, 13, c1, 3D);
            Category.seed("Screen Protectors", "default/ic_laptops.png", "default/ic_laptops.png", 1L, cat11.id, 14, c1, 3D);
            Category.seed("Batteries & Chargers", "default/ic_power_bank.png", "default/ic_power_bank.png", 1L, cat11.id, 15, c1, 3D);

            Category.seed("Storage", "default/ic_storage.png", "default/ic_storage.png", 1L, cat21.id, 16, c2, 3D);
            Category.seed("Computer Components", "default/ic_computer.png", "default/ic_computer.png", 1L, cat21.id, 17, c2, 3D);
            Category.seed("Computer Accessories", "default/ic_headset.png", "default/ic_headset.png", 1L, cat21.id, 18, c2, 3D);
            Category.seed("Printers", "default/ic_printer.png", "default/ic_printer.png", 1L, cat21.id, 19, c2, 3D);

            Category.seed("Sport & Action Cameras", "default/ic_camera_acc.png", "default/ic_camera_acc.png", 1L, cat31.id, 20, c3, 3D);
            Category.seed("Point & Shoot Cameras", "default/ic_cameras.png", "default/ic_cameras.png", 1L, cat31.id, 21, c3, 3D);
            Category.seed("DSLR Cameras", "default/ic_camera_acc.png", "default/ic_camera_acc.png", 1L, cat31.id, 22, c3, 3D);
            Category.seed("Mirrorless Cameras", "default/ic_cameras.png", "default/ic_cameras.png", 1L, cat31.id, 23, c3, 3D);
            Category.seed("Drones", "default/ic_power_bank.png", "default/ic_power_bank.png", 1L, cat31.id, 24, c3, 3D);
            Category.seed("Instant Cameras", "default/ic_cameras.png", "default/ic_cameras.png", 1L, cat31.id, 25, c3, 3D);

            Category.seed("4k TVs", "default/ic_tv.png", "default/ic_tv.png", 1L, cat41.id, 26, c4, 3D);
            Category.seed("Smart TVs", "default/ic_laptops.png", "default/ic_laptops.png", 1L, cat41.id, 27, c4, 3D);
            Category.seed("LED TVs", "default/ic_tv.png", "default/ic_tv.png", 1L, cat41.id, 28, c4, 3D);
            Category.seed("LCD TVs", "default/ic_tv.png", "default/ic_tv.png", 1L, cat41.id, 29, c4, 3D);

        }
    }

    public static void seedTestingBanner() {
        if (MostPopularBanner.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);
            Date curr = new Date(System.currentTimeMillis());

            Product product = Product.find.byId(new Long(1));
            Category category = product.category.parentCategory;

            MostPopularBanner.seed(1, "Most Popular 1", user, category, product, "banner/most_popular1.jpg");
            MostPopularBanner.seed(2, "Most Popular 2", user, category, product, "banner/most_popular2.jpg");
            MostPopularBanner.seed(3, "Most Popular 3", user, category, product, "banner/most_popular3.jpg");
            MostPopularBanner.seed(4, "Most Popular 4", user, category, product, "banner/most_popular4.jpg");
            MostPopularBanner.seed(5, "Most Popular 5", user, category, product, "banner/most_popular5.jpg");
            MostPopularBanner.seed(6, "Most Popular 6", user, category, product, "banner/most_popular6.jpg");
            MostPopularBanner.seed(7, "Most Popular 7", user, category, product, "banner/most_popular7.jpg");
        }

        if (BannerMostPopular.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);
            Date curr = new Date(System.currentTimeMillis());
            BannerMostPopular mostPopular = new BannerMostPopular();
            mostPopular.status = true;
            mostPopular.isDeleted = false;
            mostPopular.product1 = Product.find.byId(new Long(1));
            mostPopular.product2 = Product.find.byId(new Long(1));
            mostPopular.product3 = Product.find.byId(new Long(1));
            mostPopular.product4 = Product.find.byId(new Long(1));
            mostPopular.product5 = Product.find.byId(new Long(1));
            mostPopular.product6 = Product.find.byId(new Long(1));
            mostPopular.product7 = Product.find.byId(new Long(1));
            mostPopular.imageUrl1 = "banner/most_popular1.jpg";
            mostPopular.imageUrl2 = "banner/most_popular2.jpg";
            mostPopular.imageUrl3 = "banner/most_popular3.jpg";
            mostPopular.imageUrl4 = "banner/most_popular4.jpg";
            mostPopular.imageUrl5 = "banner/most_popular5.jpg";
            mostPopular.imageUrl6 = "banner/most_popular6.jpg";
            mostPopular.imageUrl7 = "banner/most_popular7.jpg";
            List<Category> listsCategory = new ArrayList<>();
            Category category = mostPopular.product1.category;
            listsCategory.add(category.parentCategory);
            mostPopular.categories = listsCategory;
            List<Merchant> listsMerchant = new ArrayList<>();
            listsMerchant.add(mostPopular.product1.merchant);
            mostPopular.merchants = listsMerchant;
            mostPopular.save();
        }

        if (Banner.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            Product product = Product.find.byId(new Long(1));
            Category category = product.category;
            Merchant merchant = product.merchant;

            UserCms user = UserCms.find.byId(1L);
            Date curr = new Date(System.currentTimeMillis());
            Calendar cban1 = Calendar.getInstance();
            cban1.setTime(curr);
            cban1.add(Calendar.YEAR, 1);

            try {
                String image = "ban_020317-203939_bluboo-dual.jpg";
                Banner.seed(1, "New Year Sale", 1, image, Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Banner.seed(2, "Amazing Sale", 1, image, Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Banner.seed(3, "Xiami Yi Dome", 1, image, Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Banner.seed(4, "New Year Revolution", 1, image, Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Banner.seed(5, "New Year New Style", 1, image, Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);

                Banner.seed(1, "Summer Sale", 2, "banner/260317-140959_summer-sale.jpg", Arrays.asList(merchant), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }

        if (CategoryBanner.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);
            List<String> collors = Arrays.asList("#ce3035", "#863aeb", "#34acef", "#ff6633");
            List<String> names = Arrays.asList("Bluetooth Keyboard", "Selfie Light", "Tablet PC Holder",
                    "Ponsel Reparator", "Wireless Charger", "Cleansing Kits", "4 in 1 Stylus");
            for (int i=1; i<=4; i++){
                Product product = Product.find.byId((long) i);
                Category category = product.category;

                CategoryBanner categoryBanner = new CategoryBanner();
                categoryBanner.category = category.parentCategory.parentCategory;
                categoryBanner.imageDescription = categoryBanner.imageKeyword = categoryBanner.imageTitle =
                        categoryBanner.imageName = categoryBanner.category.name;
                categoryBanner.status = true;
                categoryBanner.sequence = i;
                categoryBanner.userCms = user;
                categoryBanner.imageUrl = "banner/icon-cat-"+i+".png";
                categoryBanner.color = collors.get(i-1);
                categoryBanner.isDeleted = false;
                categoryBanner.save();
                String slug = category.parentCategory.parentCategory.id+"-";

                CategoryBannerDetail detail1 = new CategoryBannerDetail();
                detail1.isDeleted = false;
                detail1.title = detail1.description = detail1.keyword = detail1.name = "Special Promo Tablet";
                detail1.slug = CommonFunction.slugGenerate(slug+detail1.name);
                detail1.caption = "";
                detail1.userCms = user;
                detail1.sequence = 1;
                detail1.categoryBanner = categoryBanner;
                detail1.category = category.parentCategory;
                detail1.subCategory = category;
                detail1.brand = product.brand;
                detail1.product = product;
                detail1.imageUrl = "banner/cat"+i+"_1.png";
                detail1.save();

                CategoryBannerDetail detail2 = new CategoryBannerDetail();
                detail2.title = detail2.description = detail2.keyword = detail2.name = "Parade Smart Phone";
                detail2.slug = CommonFunction.slugGenerate(slug+detail2.name);
                detail2.caption = "";
                detail2.isDeleted = false;
                detail2.userCms = user;
                detail2.sequence = 2;
                detail2.categoryBanner = categoryBanner;
                detail2.category = category.parentCategory;
                detail2.subCategory = category;
                detail2.brand = product.brand;
                detail2.product = product;
                detail2.imageUrl = "banner/cat"+i+"_2.png";
                detail2.save();

                for (int j=1; j<=7; j++){
                    CategoryBannerDetail detail = new CategoryBannerDetail();
                    detail.title = detail.description = detail.keyword = detail.name = names.get(j-1);
                    detail.caption = j==1 ? "With Leather Case" : "";
                    detail.slug = CommonFunction.slugGenerate(slug+detail.name);
                    detail.categoryBanner = categoryBanner;
                    detail.isDeleted = false;
                    detail.userCms = user;
                    detail.sequence = j+2;
                    detail.category = category.parentCategory;
                    detail.subCategory = category;
                    detail.category = category;
                    detail.brand = product.brand;
                    detail.product = product;
                    detail.imageUrl = "banner/cat-product-0"+j+".png";
                    detail.save();
                }
            }

        }

        if (SubCategoryBanner.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);
            Transaction txn = Ebean.beginTransaction();
            try {
                Category.find.where().eq("level", 3).findList().forEach(a->{
                    SubCategoryBanner scb = new SubCategoryBanner();
                    scb.category = a;
                    scb.userCms = user;
                    scb.status = true;
                    scb.createdAt = new Date();
                    scb.updatedAt = new Date();
                    scb.save();

                    List<Product> products = Product.find.where().eq("category_id", a.id).setMaxRows(1).findList();

                    for (int i=1; i<=6; i++){
                        SubCategoryBannerDetail.seed(i, scb, user, products);
                    }

                });
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }

        }

        if (HighlightBanner.find.findRowCount() == 0) {
            UserCms user = UserCms.find.byId(1L);
            Date curr = new Date(System.currentTimeMillis());
            List<Category> listsCategory = new ArrayList<>();
            List<Merchant> listsMerchant = new ArrayList<>();
            List<Product> listsProduct = new ArrayList<>();

            Product product = Product.find.byId(1L);
            listsCategory.add(product.category);
            listsMerchant.add(product.merchant);
            listsProduct.add(product);

            HighlightBanner.seed("Special Promotion",0L,1,listsMerchant, new ArrayList<>(), new ArrayList<>());
            HighlightBanner.seed("Flash Sales",1L,1,listsMerchant, listsCategory, new ArrayList<>());
            HighlightBanner.seed("Best Deals",1L,2,new ArrayList<>(), new ArrayList<>(), listsProduct);

        }
    }


    // merchant dummy data
    public static void seedTestingMerchant() {
        if (Merchant.find.findRowCount() == 0) {
            try {
                District d1 = District.find.byId(1l);
                District d2 = District.find.byId(6l);

                Township t1 = Township.find.where().eq("district", d1).setMaxRows(1).findUnique();
                Township t2 = Township.find.where().eq("district", d2).setMaxRows(1).findUnique();

                List<Courier> couriers = Courier.find.all();
                Merchant merchant2 = new Merchant();
                merchant2.id = -1L;
                merchant2.address = "All around you.";
                merchant2.anchor = true;
                merchant2.cityName = "Bandung";
                merchant2.postalCode = "12345";
                merchant2.commissionType = "Commision type";
                merchant2.companyName = "PT Hokeba";
                merchant2.display = true;
                merchant2.logo = "";
                merchant2.merchantCode = "hokeba1";
                merchant2.merchantUrlPage = "www.hokeba.com";
                merchant2.metaDescription = "Hokeba";
                merchant2.fullName = merchant2.name = "PT Hokeba";
                merchant2.domain = "www.hokeba.com";
                merchant2.email = "hokeba@hokeba.com";
                merchant2.password = Encryption.EncryptAESCBCPCKS5Padding("password");
                merchant2.phone = "69-0721-***";
                merchant2.productAvailability = new Long(1);
                merchant2.productHandledAndShippedDescription = "Product will be packaged and send by flying courier";
                merchant2.productHandledDescription = "Product will be packaged using iron bars";
                merchant2.productQuality = new Long(5);
                merchant2.productShippedDescription = "Product will be send by flying courier";
                // merchant2.province = "Jawa Barat";
                merchant2.quickResponse = new Long(5);
                merchant2.rating = 0D;
                merchant2.status = "Good";
                merchant2.story = "*poof";
                merchant2.isActive = false;
                merchant2.type = "Merchant type";
                merchant2.url = "Url to merchant information at this site?";
                merchant2.urlBanner = "Url to merchant banner";
                merchant2.countRating = 0;
                merchant2.rating = 0D;
                merchant2.district = d2;
                merchant2.township = t2;
                merchant2.couriers = couriers;
                merchant2.save();

                Merchant merchant = new Merchant();
                merchant.address = "All around you.";
                merchant.anchor = true;
                merchant.cityName = "Bandung";
                merchant.postalCode = "12345";
                merchant.commissionType = "Commision type";
                merchant.companyName = "Merchant Company Name";
                merchant.display = true;
                merchant.logo = "";
                merchant.merchantCode = "soloMerch1";
                merchant.merchantUrlPage = "Url to merchant home page.";
                merchant.metaDescription = "Meta description";
                merchant.fullName = merchant.name = "Dummy Merchant Name";
                merchant.domain = "www.hokeba.com/dummy";
                merchant.email = "merchant@hokeba.com";
                merchant.password = Encryption.EncryptAESCBCPCKS5Padding("password");
                merchant.phone = "69-0721-***";
                merchant.productAvailability = new Long(1);
                merchant.productHandledAndShippedDescription = "Product will be packaged and send by flying courier";
                merchant.productHandledDescription = "Product will be packaged using iron bars";
                merchant.productQuality = new Long(5);
                merchant.productShippedDescription = "Product will be send by flying courier";
                // merchant.province = "Jawa Barat";
                merchant.quickResponse = new Long(5);
                merchant.rating = 0D;
                merchant.status = "Good";
                merchant.story = "*poof";
                merchant.isActive = true;
                merchant.type = "Merchant type";
                merchant.url = "Url to merchant information at this site?";
                merchant.urlBanner = "Url to merchant banner";
//                merchant.odooId = 10;
                merchant.countRating = 0;
                merchant.rating = 0D;
                merchant.district = d1;
                merchant.township = t1;
                merchant.couriers = couriers;
                merchant.save();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // vendor dummy data
    public static void seedTestingVendor() {
        if (Vendor.find.findRowCount() == 0) {
            try {
                Vendor vendor = new Vendor();
                vendor.code = "VE0000001";
                vendor.fullName = "PT. Agusta Electronics";
                vendor.name = "PT. Agusta Electronics";
                vendor.isDeleted = false;
                vendor.status = true;
                vendor.userCms = UserCms.find.byId(new Long(1));
//                vendor.odooId = 8;
                vendor.countRating = 0;
                vendor.rating = 0D;

                vendor.cityName = "Bandung";
                vendor.postalCode = "12345";
                vendor.address = "Alamat 1";
                vendor.email = "vendor1@hokeba.com";
                vendor.province = "Jawa Barat";
                vendor.phone = "62-0721-***";
                vendor.save();

                Vendor vendor2 = new Vendor();
                vendor2.code = "VE0000002";
                vendor2.fullName = "PT. Permata Light";
                vendor2.name = "PT. Permata Light";
                vendor2.isDeleted = false;
                vendor2.status = true;
                vendor2.userCms = UserCms.find.byId(new Long(1));
//                vendor2.odooId = 9;
                vendor2.countRating = 0;
                vendor2.rating = 0D;

                vendor2.cityName = "Bandung";
                vendor2.postalCode = "12345";
                vendor2.address = "Alamat 2";
                vendor2.email = "vendor2@hokeba.com";
                vendor2.province = "Jawa Barat";
                vendor2.phone = "62-0721-***";
                vendor2.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static void seedTestingBrand() {
        if (Brand.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                Brand.seed("Xiaomi", "brand_020317-084646_xiaomi.jpg", user, 2);
                Brand.seed("Samsung", "brand_020317-133718_samsung.jpg", user, 3);
                Brand.seed("Iphone", "brand_020317-133748_iphone.jpg", user, 4);
                Brand.seed("OnePlus", "brand_020317-133839_oneplus.jpg", user, 5);
                Brand.seed("Huawei", "brand_020317-133945_huawei.jpg", user, 6);
                Brand.seed("LG", "brand_020317-134028_lg.jpg", user, 7);
                Brand.seed("Asus", "brand_020317-134110_asus.jpg", user, 8);
                Brand.seed("HTC", "brand_020317-134158_htc.jpg", user, 9);
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingFooter() {
        if (Footer.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                Footer.seed("About Hokeba", "Left", user);
                Footer.seed("Hokeba Select", "Left", user);
                Footer.seed("Affiliate Select", "Left", user);
                Footer.seed("Partners Promotions", "Left", user);
                Footer.seed("Terms & Conditions", "Left", user);
                Footer.seed("Privacy Policy", "Left", user);
                Footer.seed("Press & Media", "Left", user);

                Footer.seed("Help Center", "Middle", user);
                Footer.seed("Payment", "Middle", user);
                Footer.seed("How to Buy", "Middle", user);
                Footer.seed("Shipping & Delivery", "Middle", user);
                Footer.seed("International Product Policy", "Middle", user);
                Footer.seed("How to Return", "Middle", user);
                Footer.seed("Contact Us", "Middle", user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingPromo() {
        if (Promo.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            Product product = Product.find.byId(new Long(1));
            Category category = product.category;
            Brand brand = product.brand;
            Merchant merchant = product.merchant;

            UserCms user = UserCms.find.byId(1L);
            Date curr = new Date(System.currentTimeMillis());
            Calendar cban1 = Calendar.getInstance();
            cban1.setTime(curr);
            cban1.add(Calendar.YEAR, 1);

            try {
                Promo.seed(1, "Promo 1", "promo/product-01.png", Arrays.asList(merchant), Arrays.asList(brand), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Promo.seed(2, "Promo 2", "promo/product-02.png", Arrays.asList(merchant), Arrays.asList(brand), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Promo.seed(3, "Promo 3", "promo/product-03.png", Arrays.asList(merchant), Arrays.asList(brand), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);
                Promo.seed(4, "Promo 4", "promo/product-04.png", Arrays.asList(merchant), Arrays.asList(brand), Arrays.asList(category),
                        Arrays.asList(product), curr, cban1.getTime(), user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingStatic() {
        if (StaticPage.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                StaticPage.seed("About Hokeba", "", user);
                StaticPage.seed("Hokeba Select", "", user);
                StaticPage.seed("Affiliate Select", "", user);
                StaticPage.seed("Partners Promotions", "", user);
                StaticPage.seed("Terms & Conditions", "", user);
                StaticPage.seed("Privacy Policy", getPrivacyPolicy(), user);
                StaticPage.seed("Press & Media", "", user);

                StaticPage.seed("Help Center", "", user);
                StaticPage.seed("Payment", "", user);
                StaticPage.seed("How to Buy", "", user);
                StaticPage.seed("Shipping & Delivery", "", user);
                StaticPage.seed("International Product Policy", "", user);
                StaticPage.seed("How to Return", "", user);
                StaticPage.seed("Contact Us", "", user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }
    static void seedTestingSeo() {
        if (SeoPage.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                SeoPage.seed(getSeoDefault(), Long.parseLong("1"));

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingArticle() {
        if (Article.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();

            try {
                UserCms user = UserCms.find.byId(1L);
                ArticleCategory artc = new ArticleCategory("Others");
                artc.userCms = user;
                artc.save();

                String shortDesc = "Hai Guys! Suka berfoto pakai kamera smartphone kamu? Kalau iya, pastinya kamu juga suka ngedit foto langsung di smartphone android juga kan. Sekarang ini sudah banyaaaaak sekali aplikasi untuk meng-edit foto dan video yang tersedia untuk dapat kita nikmati fiturnya.";
                Article.seed("10 Aplikasi Edit Foto Terbaik & Terbaru Di Android", artc, getContentArticle(),
                        "article/260317-164948_10-aplikasi-edit-foto-terbaik-terbaru-di-android.jpg", shortDesc,
                        "article_thumbnail/260317-164950_10-aplikasi-edit-foto-terbaik-terbaru-di-android.jpg", user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingProductGroup() {
        if (ProductGroup.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();

            try {
                UserCms user = UserCms.find.byId(1L);
                ProductGroup.seed("Product Group 1", null, user);
                ProductGroup.seed("Product Group 2", null, user);
                ProductGroup.seed("Product Group 3", null, user);
                ProductGroup.seed("Product Group 4", null, user);

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingProductReview() {
        if (ProductReview.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();

            try {

                ProductReview review = new ProductReview("Good tv", "Good tv", 5, true, "A", UserCms.find.byId(new Long(1)), "", Member.find.byId(new Long(1)), Product.find.byId(new Long(18)));
                review.save();

                ProductReview review2 = new ProductReview("Top product", "Top product", 5, true, "A", UserCms.find.byId(new Long(1)), "", Member.find.byId(new Long(2)), Product.find.byId(new Long(18)));
                review2.save();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static String getSeoDefault(){
        return "<div class=\"box-bottom-footer\">\n" +
                "<h2>Hokeba - Online Shopping Mall in Myanmar</h2>\n" +
                "\n" +
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"box-bottom-footer\">\n" +
                "<h2>Hokeba - Effortless Shopping</h2>\n" +
                "\n" +
                "<p>Hokeba aims to make your shopping experience effortless. From browsing to purchasing of products on Hokeba site, you can enjoy a seamless journey that helps you get what you are looking for. To make it even more convenient, we have launched our mobile app for users to access the wide selection of products and to keep users updated of the latest promotion.</p>\n" +
                "</div>\n";
    }

    static String getContentArticle(){
        return "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\"><span style=\"box-sizing: border-box; line-height: 1.5em;\">Hai Guy&rsquo;s! Suka berfoto pakai kamera smartphone kamu? Kalau iya, pastinya kamu juga suka<span>&nbsp;</span></span><em style=\"box-sizing: border-box; line-height: 1.5em;\">ngedit</em><span style=\"box-sizing: border-box; line-height: 1.5em;\"><span>&nbsp;</span>foto langsung di<span>&nbsp;</span></span><a href=\"http://www.lazada.co.id/beli-smartphone/?utm_source=officialblog&amp;utm_medium=blog&amp;utm_campaign=10aplikasi\" style=\"box-sizing: border-box; background: 0px 0px; color: rgb(66, 139, 202); text-decoration: none; line-height: 1.5em;\" target=\"_blank\">smartphone android</a><span style=\"box-sizing: border-box; line-height: 1.5em;\">&nbsp;juga kan. Sekarang ini sudah banyaaaaak sekali aplikasi untuk meng-edit foto dan video yang tersedia untuk dapat kita nikmati fiturnya.</span></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"aplikasi edit foto android terbaik\" class=\"aligncenter\" height=\"409\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-terbaik.jpg\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"617\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Sayangnya, sangking banyaknya jejeran aplikasi edit foto dan video yang tersedia, kadang kita malah galau memilih yang mana aplikasi terbaik untuk kita download. Daripada kamu muter-muter bingung mencari mana yang terbaik, kali ini blog lazada akan menyajikan aplikasi edit foto Android terbaru dan terbaik yang lagi&nbsp;<em style=\"box-sizing: border-box;\">hits,&nbsp;</em>alias sedang populer digunakan banyak orang (editor Choice&rsquo;s!). Cekidot ya!</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">VSCO</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"aplikasi edit foto VSCO CAM\" class=\"size-full wp-image-12485 aligncenter\" height=\"804\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-VSCO-CAM.jpg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-VSCO-CAM.jpg 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-VSCO-CAM-116x150.jpg 116w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-VSCO-CAM-464x600.jpg 464w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-VSCO-CAM-300x388.jpg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Jika kita cermati, tema edit foto yang sedang popular adalah mengubah warna foto menjadi lebih &ldquo;oldschool&rdquo;/warna-warna jadul. Aplikasi edit foto<span>&nbsp;</span><strong style=\"box-sizing: border-box; font-weight: 700;\">VSCO<span>&nbsp;</span></strong>menyediakan filter warna-warna tersebut dengan lengkap dan sempuna. Tampilan (UI) aplikasi VSCO juga sudah<span>&nbsp;</span><em style=\"box-sizing: border-box;\">flat-design,</em><span>&nbsp;</span>sehingga sangat nyaman digunakan dan enak dilihat. Menggunakan aplikasi ini juga tidak membutuhkan<span>&nbsp;</span><em style=\"box-sizing: border-box;\">skill,&nbsp;</em>sekali mencoba pasti hasil foto kamu langsung tampak seperti sudah di edit oleh seorang profesional!</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Tiny Planet</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"Aplikasi edit foto-Tiny-Planet-FX-Android\" class=\"size-full wp-image-12487 aligncenter\" height=\"917\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-Tiny-Planet-FX-Android.jpg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-Tiny-Planet-FX-Android.jpg 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-Tiny-Planet-FX-Android-102x150.jpg 102w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-Tiny-Planet-FX-Android-407x600.jpg 407w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-Tiny-Planet-FX-Android-300x442.jpg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Yap, foto keren diatas bisa kamu hasilkan langsung di smartphone kamu, Android maupun iPhone.<span>&nbsp;</span><strong style=\"box-sizing: border-box; font-weight: 700;\">Tiny Planet &nbsp;FX</strong><span>&nbsp;</span>menyediakan fitur edit foto yang unik dan lain daripada yang lain. Lihat saja foto diatas, Anda cukup membidik lingkungan sekitar Anda secara terus menerus, dan Tiny Plane FX akan melakukan &ldquo;magic&rdquo;-nya! Ayo buat teman-temanmu terpukau dengan aplikasi ini.</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Diptic</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"diptic-aplikasi edit foto android\" class=\"size-full wp-image-12489 aligncenter\" height=\"1064\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android.jpg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android.jpg 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android-88x150.jpg 88w, http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android-351x600.jpg 351w, http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android-599x1024.jpg 599w, http://blog.lazada.co.id/wp-content/uploads/2013/11/diptic-aplikasi-edit-foto-android-300x513.jpg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Nah, kalau aplikasi edit foto yang satu ini beda lagi fiturnya.<span>&nbsp;</span><strong style=\"box-sizing: border-box; font-weight: 700;\">Diptic</strong><span>&nbsp;</span>dapat menggabungkan dua buah atau lebih foto-foto kamu seperti terlihat gambar diatas. Selain itu Diptic juga popular digunakan untuk membuat<span>&nbsp;</span><em style=\"box-sizing: border-box;\">Avatar</em><span>&nbsp;</span>foto sosial media menjadi berbingkai bulat, X, dll, seperti sreenshot diatas. Foto bersama teman-teman jadi lebih gaul lho kalau pakai aplikasi yang satu ini, narsis juga bisa lebih maksimal. Kamu bisa foto banyak dan menggabungkannya layaknya bingkai foto.</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Phonto</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"aplikasi edit foto android phonto\" class=\"size-full wp-image-12492 aligncenter\" height=\"511\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-phonto.jpeg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-phonto.jpeg 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-phonto-150x123.jpeg 150w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-phonto-600x493.jpeg 600w, http://blog.lazada.co.id/wp-content/uploads/2013/11/aplikasi-edit-foto-android-phonto-300x246.jpeg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Menulis curahan hati adalah hal paling umum dilakukan di sosial media, hmmm menulis curahan hati diatas foto yang cocok tentunya ide yang lebih bagus lagi!<span>&nbsp;</span><strong style=\"box-sizing: border-box; font-weight: 700;\">Phonto&nbsp;</strong>adalah aplikasi yang dapat mewujudkannya. Phonto merupakan aplikasi edit foto pemberi<span>&nbsp;</span><em style=\"box-sizing: border-box;\">text</em><span>&nbsp;</span>pada foto anda. Anda dapat memilih<span>&nbsp;</span><em style=\"box-sizing: border-box;\">font</em><span>&nbsp;</span>dan warna font tulisan yang tampil pada foto yang anda pilih. Navigasinya juga sangat mudah dipelajari, menurut saya ini adalah aplikasi edit foto terbaik bagi yang ingin menulis curahan hati pada sebuah gambar.</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">InstaVideo</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"Aplikasi edit foto video insta video Android\" class=\"size-full wp-image-12493 aligncenter\" height=\"470\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-video-insta-video-Android.png\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-video-insta-video-Android.png 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-video-insta-video-Android-150x113.png 150w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-video-insta-video-Android-600x453.png 600w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-video-insta-video-Android-300x227.png 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Awalnya Instagram yang kini tenar adalah aplikasi yang ekslusif untuk iOS saja (iPhone, iPad, iPod), namun seiring perkembangannya, Intagram juga dapat dipakai di platform Android. Bahkan kini ada<span>&nbsp;</span><strong style=\"box-sizing: border-box; font-weight: 700;\">Insta Video</strong><span>&nbsp;</span>yang membuat anda bisa menggabungkan beberapa video dalam satu frame/bingkai. Foto tersebut nantinya akan dapat di-upload ke berbagai sosial media tenar seperti path, instagram, dll. Menggunakan Instavideo betul-betul menarik dan sangat berkesan.</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Camera360</h2>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"Aplikasi edit foto terbaik android\" class=\"size-full wp-image-12496 aligncenter\" height=\"825\" sizes=\"(max-width: 622px) 100vw, 622px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-terbaik-android1.png\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-terbaik-android1.png 622w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-terbaik-android1-113x150.png 113w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-terbaik-android1-452x600.png 452w, http://blog.lazada.co.id/wp-content/uploads/2013/11/Aplikasi-edit-foto-terbaik-android1-300x398.png 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"622\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\"><strong style=\"box-sizing: border-box; font-weight: 700;\">Camera 360</strong><span>&nbsp;</span>adalah aplikasi edit foto dengan fitur terlengkap dibanding yang lainya, telah dipakai oleh jutaan orang dan terbukti masih belum ditinggalkan. Camera 360 tersedia dalam dua pilihan, versi gratis dan versi berbayar, namun menurut kami, versi gratisnya juga sudah cukup mumpuni untuk kita dapat menghasilkan foto yang super kreatif seperti foto diatas.</p>\n" +
                "\n" +
                "<h2 style=\"box-sizing: border-box; font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-weight: 500; line-height: 1.1; color: rgb(51, 51, 51); margin-top: 20px; margin-bottom: 10px; font-size: 30px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">MomentCam</h2>\n" +
                "\n" +
                "<div class=\"wp-caption aligncenter\" id=\"attachment_12499\" style=\"box-sizing: border-box; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); width: 620px;\"><img alt=\"momentcam edit foto android-2\" class=\"size-full wp-image-12499 \" height=\"350\" sizes=\"(max-width: 610px) 100vw, 610px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-2.jpg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-2.jpg 610w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-2-150x86.jpg 150w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-2-600x344.jpg 600w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-2-300x172.jpg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" width=\"610\" />\n" +
                "<p class=\"wp-caption-text\" style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5;\">Sumber: Google image dari http://www.kotakgame.com/gamebox/detail/30/3760/</p>\n" +
                "</div>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-align: center;\"><img alt=\"momentcam edit foto android\" class=\"alignnone size-full wp-image-12498\" sizes=\"(max-width: 621px) 100vw, 621px\" src=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android.jpg\" srcset=\"http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android.jpg 621w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-150x82.jpg 150w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-600x329.jpg 600w, http://blog.lazada.co.id/wp-content/uploads/2013/11/momentcam-edit-foto-android-300x165.jpg 300w\" style=\"box-sizing: border-box; border: 0px; vertical-align: middle;\" /></p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Ini dia aplikasi edit foto paling<span>&nbsp;</span><em style=\"box-sizing: border-box;\">hits!<span>&nbsp;</span></em>sata ini. Tentunya&nbsp;paling kocak diantara lainnya! Lihat saja, kamu bisa jadikan muka kamu atau teman kamu menjadi puluhan karakter kartun karitur yang bakal bikin ngakak kamu dan teman-teman. Silahkan lihat screnshot diatas.</p>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">3 rekomendasi aplikasi edit foto lainnya di Android lainnya adalah:</p>\n" +
                "\n" +
                "<ul style=\"box-sizing: border-box; margin-top: 0px; margin-bottom: 10px; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">\n" +
                "\t<li style=\"box-sizing: border-box;\"><span style=\"box-sizing: border-box; font-size: small;\">Image Editor</span></li>\n" +
                "\t<li style=\"box-sizing: border-box;\"><span style=\"box-sizing: border-box; font-size: small;\">Camera+ (Camera Studio)</span></li>\n" +
                "\t<li style=\"box-sizing: border-box;\"><span style=\"box-sizing: border-box; font-size: small;\">Pixlr-O-Matic</span></li>\n" +
                "</ul>\n" +
                "\n" +
                "<p style=\"box-sizing: border-box; margin: 0px 0px 10px; line-height: 1.5; color: rgb(51, 51, 51); font-family: &quot;Segoe UI&quot;, proxima-nova, proxima-nova-1, proxima-nova-2, &quot;Proxima Nova&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: justify; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255);\">Demikian aplikasi-aplikasi edit foto terbaik saat ini, semoga bermanfaat untuk kamu ya<span>&nbsp;</span><img alt=\"\uD83D\uDE42\" class=\"emoji\" draggable=\"false\" src=\"https://s.w.org/images/core/emoji/2/svg/1f642.svg\" style=\"box-sizing: border-box; border: none !important; vertical-align: -0.1em !important; display: inline !important; box-shadow: none !important; height: 1em !important; width: 1em !important; margin: 0px 0.07em !important; background: none !important; padding: 0px !important;\" /></p>\n";
    }

    static String getPrivacyPolicy(){
        return "<h4>We take our customer's privacy seriously and we will only collect, record, hold, store, disclose, transfer and use your personal information as outlined below.</h4>\n" +
                "            <p class=\"main-info\">\n" +
                "              Data protection is a matter of trust and your privacy is important to us. We shall therefore only use your name and other information, which relates to you in the manner set out in this Privacy Policy. We will only collect information where it is necessary for us to do so and we will only collect information if it is relevant to our dealings with you.\n" +
                "            </p>\n" +
                "            <p class=\"main-info\">\n" +
                "              We will only keep your information for as long as we are either required to by law or as is relevant for the purposes for which it was collected.\n" +
                "            </p>\n" +
                "            <p class=\"main-info\">\n" +
                "              You can visit the Platform (as defined in the Terms of Use) and browse without having to provide personal details. During your visit to the Platform you remain anonymous and at no time can we identify you unless you have an account on the Platform and log on with your user name and password.\n" +
                "            </p>\n" +
                "            <p class=\"main-info\">\n" +
                "              Hokeba is committed to complying with the Personal Data Protection Act 2012.\n" +
                "            </p>\n" +
                "            <p class=\"main-info\">\n" +
                "              If you have any comments, suggestions or complaints, you may contact us (and our Data Protection Officer) by e-mail at customer@hokeba.com.\n" +
                "            </p>\n" +
                "\n" +
                "            <div class=\"clearfix row privacy-menus\">\n" +
                "              <div class=\"col-md-3 menus\">\n" +
                "                <div id=\"searchbycharacter\">\n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"A\">Collection of Personal Information</a> \n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"B\">Disclosure of Personal Information</a> \n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"C\">Withdrawal of Consent Accessing and Updating Your Personal Information</a> \n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"D\">Security of Your Personal Information</a>\n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"E\">Collection of Computer Data not necessarily Personal Information</a>\n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"F\">Changes to the Privacy Policy</a>\n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"G\">Hokeba's Right</a>\n" +
                "                  <a class=\"searchbychar\" href=\"#\" data-target=\"H\">Contacting Hokeba</a>\n" +
                "                </div>\n" +
                "              </div>\n" +
                "\n" +
                "              <div class=\"col-md-9 values\">\n" +
                "                <div id=\"A\" class=\"box-values\">\n" +
                "                  <h3>Collection of Personal Information</h3>\n" +
                "                  <p>\n" +
                "                    When you create a Hokeba account, or otherwise provide us with your personal information through the Platform, the personal information we collect may include your:\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    <strong>Name</strong><br/>\n" +
                "                    <strong>Delivery Address</strong><br/>\n" +
                "                    <strong>Email Address</strong><br/>\n" +
                "                    <strong>Contact Number</strong><br/>\n" +
                "                    <strong>Mobile Number</strong><br/>\n" +
                "                    <strong>Date of Birth</strong><br/>\n" +
                "                    <strong>Gender</strong>\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    You must only submit to us, our authorised agent or the Platform, information which is accurate and not misleading and you must keep it up to date and inform us of changes (more information below). We reserve the right to request for documentation to verify the information provided by you.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    We will only be able to collect your personal information if you voluntarily submit the information to us. If you choose not to submit your personal information to us or subsequently withdraw your consent to our use of your personal information, we may not be able to provide you with our Services. You may access and update your personal information submitted to us at any time as described below.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    If you provide personal information of any third party to us, we assume that you have obtained the required consent from the relevant third party to share and transfer his/her personal information to us.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    If you sign up for Hokeba using your social media account or link your Hokeba account to your social media account or use certain other Hokeba social media features, we may access information about you which you have voluntarily provided under your social media provider in accordance with the provider's policies and we will manage your personal data which we have collected in accordance with Hokeba's privacy policy.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"B\" class=\"box-values\">\n" +
                "                  <h3>Use and Disclosure of Personal Information</h3>\n" +
                "                  <p>\n" +
                "                    The personal information we collect from you will be used, or shared with third parties (including related companies, third party service providers, and third party sellers), for some or all of the following purposes:\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    To facilitate your use of the Services (as defined in the Terms of Use) and/or access to the Platform;<br/>\n" +
                "                    To process orders you submit through the Platform, whether the products are sold by Hokeba or a third party seller. Payments that you make through the Platform for products, whether sold by Hokeba or a third party seller, will be processed by our agent;<br/>\n" +
                "                    To deliver the products you have purchased through the Platform, whether sold by Hokeba or a third party seller. We may pass your personal information on to a third party in order to make delivery of the product to you (for example to our courier or supplier), whether the product is sold through the Platform by Hokeba or a third party seller;<br/>\n" +
                "                    To update you on the delivery of the products, whether sold through the Platform by Hokeba or a third party seller, and for customer support purposes;<br/>\n" +
                "                    To compare information, and verify with third parties in order to ensure that the information is accurate;<br/>\n" +
                "                    Further, we will use the information you provide to administer your account (if any) with us; verify and carry out financial transactions in relation to payments you make online; audit the downloading of data from the Platform; improve the layout and/or content of the pages of the Platform and customise them for users; identify visitors on the Platform; carry out research on our users' demographics and behaviour; provide you with information we think you may find useful or which you have requested from us, including information about our or third party sellers' products and services, provided you have indicated that you have not objected to being contacted for these purposes;<br/>\n" +
                "                    When you register an account with Hokeba or otherwise provide us with your personal information through the Platform, we will also use your personal information to send you marketing and/or promotional materials about our or third party sellers' products and services from time to time. You can unsubscribe from receiving marketing information at any time by using the unsubscribe function within the electronic marketing material. We may use your contact information to send newsletters from us and from our related companies; and<br/>\n" +
                "                    In exceptional circumstances Hokeba may be required to disclose personal information, such as when there are grounds to believe that the disclosure is necessary to prevent a threat to life or health, or for law enforcement purposes, or for fulfillment of legal and regulatory requirements and requests.<br/>\n" +
                "                    Hokeba may share your personal information with third parties and our affiliates for the abovementioned purposes, specifically, completing a transaction with you, managing your account and our relationship with you, marketing and fulfilling any legal or regulatory requirements and requests as deemed necessary by Hokeba. In sharing your personal information with them, we endeavor to ensure that the third parties and our affiliates keep your personal information secure from unauthorised access, collection, use, disclosure, or similar risks and retain your personal information only for as long as they need your personal information to achieve the abovementioned purposes.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    In disclosing or transferring your personal information to third parties and our affiliates located overseas, Hokeba take steps to ensure that the receiving jurisdiction has in place a standard of protection accorded to personal information that is comparable to the protection under or up to the standard of the PDPA.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Hokeba does not engage into the business of selling customers' personal information to third parties.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"C\" class=\"box-values\">\n" +
                "                  <h3>Withdrawal of Consent</h3>\n" +
                "                  <p>\n" +
                "                    You may communicate your objection to our continual use and/or disclosure of your personal information for any of the purposes and in the manner as stated above at any time by contacting us at our e-mail address below.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Please note that if you communicate your objection to our use and/or disclosure of your personal information for the purposes and in the manner as stated above, depending on the nature of your objection, we may not be in a position to continue to provide our products or services to you or perform on any contract we have with you. Our legal rights and remedies are expressly reserved in such event.\n" +
                "                  </p>\n" +
                "\n" +
                "                  <h3 class=\"no-padding-top\">Updating Your Personal Information</h3>\n" +
                "                  <p>\n" +
                "                    You can update your personal information anytime by accessing your account on the Hokeba Platform. If you do not have an account with us, you can do so by contacting us at our e-mail address above.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    We take steps to share the updates to your personal information with third parties and our affiliates with whom we have shared your personal information if your personal information is still necessary for the above stated purposes.\n" +
                "                  </p>\n" +
                "\n" +
                "                  <h3 class=\"no-padding-top\">Accessing Your Personal Information</h3>\n" +
                "                  <p>\n" +
                "                    If you would like to view the personal information we have on you or inquire about the ways in which your personal information has been or may have been used or disclosed by Hokeba within the past year, please contact us at our e-mail address below. We reserve the right to charge a reasonable administrative fee for retrieving your personal information records.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    If you have an account with Hokeba, you may access details of your order by logging into your account on the Platform. Here you can view the details of your orders that have been completed, those which are open and those which are shortly to be dispatched and administer your address details, bank details and any newsletter to which you may have subscribed. You undertake to treat your Hokeba username, password and order details confidentially and not make it available to unauthorised third parties. We cannot assume any liability for misuse of your Hokeba username, password or order details, except as stated in the Terms of Use.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"D\" class=\"box-values\">\n" +
                "                  <h3>Security of Your Personal Information</h3>\n" +
                "                  <p>\n" +
                "                    Hokeba ensures that all information collected will be safely and securely stored. We protect your personal information by:\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Restricting access to personal information<br/>\n" +
                "                    Maintaining technology products to prevent unauthorised computer access<br/>\n" +
                "                    Securely destroying your personal information when it is no longer needed for any legal or business purpose<br/>\n" +
                "                    Hokeba uses 128-bit SSL (secure sockets layer) encryption technology when processing your financial details. 128-bit SSL encryption is approximated to take at least one trillion years to break, and is the industry standard.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    If you believe that your privacy has been breached by Hokeba, please contact us at our e-mail address below.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Your password is the key to your account. Please use unique numbers, letters and special characters, and do not share your Hokeba password to anyone. If you do share your password with others, you will be responsible for all actions taken in the name of your account and the consequences. If you lose control of your password, you may lose substantial control over your personal information and other information submitted to Hokeba. You could also be subject to legally binding actions taken on your behalf. Therefore, if your password has been compromised for any reason or if you have grounds to believe that your password has been compromised, you should immediately contact us and change your password. You are reminded to log off of your account and close the browser when finished using a shared computer.\n" +
                "                  </p>\n" +
                "\n" +
                "                  <h3 class=\"no-padding-top\">Minor</h3>\n" +
                "                  <p>\n" +
                "                    Hokeba does not sell products for purchase by children. If you are under 18 years old, you may use our website only with the involvement of a parent or guardian.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"E\" class=\"box-values\">\n" +
                "                  <h3>Collection of Computer Data</h3>\n" +
                "                  <p>\n" +
                "                    Hokeba or our authorized service providers may use cookies, web beacons, and other similar technologies for storing information to help provide you with a better, faster, safer and personalized experience when you use the Services and/or access the Platform.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    When you visit Hokeba, our company servers will automatically record information that your browser sends whenever you visit a website. This data may include:\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Your computer's IP address\n" +
                "                    Browser type\n" +
                "                    Webpage you were visiting before you came to our Platform\n" +
                "                    The pages within the Platform which you visit\n" +
                "                    The time spent on those pages, items and information searched for on the Platform, access times and dates, and other statistics.\n" +
                "                    This information is collected for analysis and evaluation in order to help us improve our website and the services and products we provide.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Cookies are small text files (typically made up of letters and numbers) placed in the memory of your browser or device when you visit a website or view a message. They allow us to recognize a particular device or browser and help us to personalize the content to match your preferred interests more quickly, and to make our Services and Platform more convenient and useful to you.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Web beacons are small graphic images that may be included on our Service and the Platform. They allow us to count users who have viewed these pages so that we can better understand your preference and interests.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"F\" class=\"box-values\">\n" +
                "                  <h3>No Spam, Spyware, or Virus</h3>\n" +
                "                  <p>\n" +
                "                    Spam, spyware or virus is not allowed on Platform. Please set and maintain your communication preferences so that we send communications to you as you prefer. You are not licensed or otherwise allowed to add other users (even a user who has purchased an item from you) to your mailing list (email or physical mail) without their express consent. You should not send any messages which contain spam, spyware or virus via the Platform. If you would like to report any suspicious messages, please contact us at our email address below.\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Changes to the Privacy Policy\n" +
                "                  </p>\n" +
                "                  <p>\n" +
                "                    Hokeba shall regularly review the sufficiency of this Privacy Policy. We reserve the right to modify and change the Privacy Policy at any time. Any changes to this policy will be published on the Platform.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"G\" class=\"box-values\">\n" +
                "                  <h3>Hokeba's Right</h3>\n" +
                "                  <p>\n" +
                "                    YOU ACKNOWLEDGE AND AGREE THAT Hokeba HAS THE RIGHT TO DISCLOSE YOUR PERSONAL INFORMATION TO ANY LEGAL, REGULATORY, GOVERNMENTAL, TAX, LAW ENFORCEMENT OR OTHER AUTHORITIES OR THE RELEVANT RIGHT OWNERS, IF Hokeba HAS REASONABLE GROUNDS TO BELIEVE THAT DISCLOSURE OF YOUR PERSONAL INFORMATION IS NECESSARY FOR THE PURPOSE OF MEETING ANY OBLIGATIONS, REQUIREMENTS OR ARRANGEMENTS, WHETHER VOLUNTARY OR MANDATORY, AS A RESULT OF COOPERATING WITH AN ORDER, AN INVESTIGATION AND/OR A REQUEST OF ANY NATURE BY SUCH PARTIES. TO THE EXTENT PERMISSIBLE BY APPLICABLE LAW, YOU AGREE NOT TO TAKE ANY ACTION AND/OR WAIVE YOUR RIGHTS TO TAKE ANY ACTION AGAINST Hokeba FOR THE DISCLOSURE OF YOUR PERSONAL INFORMATION IN THESE CIRCUMSTANCES.\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "\n" +
                "                <div id=\"H\" class=\"box-values\">\n" +
                "                  <h3>Contacting Hokeba</h3>\n" +
                "                  <p>\n" +
                "                    If you wish to withdraw your consent to our use of your personal information, request access and/or correction of your personal information, have any queries, comments or concerns, or require any help on technical or cookie-related matters, please feel free to contact us (and our Data Protection Officer) at [customer@Hokeba.com].\n" +
                "                  </p>\n" +
                "                </div>\n" +
                "              </div>\n" +
                "\n" +
                "            </div>";
    }

    static void seedTestingBank() {
        if (Bank.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                Bank.seed("BCA", "HOKEBA", "03571626512", "", user, "bank/bca_logo.png");
                Bank.seed("Mandiri", "HOKEBA", "561517231", "", user, "bank/mandiri_logo.png");
                Bank.seed("BRI", "HOKEBA", "561517231", "", user, "bank/bri_logo.png");
                Bank.seed("BNI", "HOKEBA", "561517231", "", user, "bank/bni_logo.png");
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingRegion() {
        if (Region.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Long user = 1L;
                Region.seed("AYE","Ayeryarwady");
                Region.seed("BAG","Bago");
                Region.seed("MAN","Mandalay");
//                Region.seed("MAG","Magway");
//                Region.seed("SAG","Sagaing");
//                Region.seed("TAN","Tanintharyi");
//                Region.seed("YAN","Yangon");

                Region region1 = Region.find.byId(new Long(1));
                District.seed("PAT", "Pathein", region1);
                District.seed("HIN", "Hinthada", region1);
                District.seed("MYA", "Myaungmya", region1);
                District.seed("MAU", "Maubin", region1);
                District.seed("PYA", "Pyapon", region1);

                Region region2 = Region.find.byId(new Long(2));
                District.seed("BAG", "Bago", region2);
                District.seed("TAU", "Taungoo", region2);
                District.seed("PYAY", "Pyay", region2);
                District.seed("THA", "Tharrawaddy", region2);

                Region region3 = Region.find.byId(new Long(3));
                District.seed("KYA", "Kyaukse", region3);
                District.seed("MAN", "Mandalay", region3);
                District.seed("MEI", "Meiktila", region3);
                District.seed("MYI", "Myingyan", region3);
                District.seed("NYA", "Nyaung-U", region3);
                District.seed("PYI", "Pyinoolwin", region3);

                List<District> listsDistrict = District.find.findList();
                for(District district : listsDistrict){
                    for(int i = 1; i < 6; i++){
                        Township.seed(district.code+"T"+i, district.name+" Township "+i, district);
                    }
                }

                List<Township> listTownships = Township.find.findList();
                for(Township township : listTownships){
                    for(int i = 1; i < 6; i++){
                        Village.seed(township.code+"V"+i, township.name+" Village "+i, township);
                    }
                }

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }

        if (ShippingCity.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms user = UserCms.find.byId(1L);

                Region region1 = Region.find.byId(1L);
                District district11 = District.find.byId(1L);
                District district12 = District.find.byId(2L);
                Township township111 = Township.find.byId(1L);
                Township township112 = Township.find.byId(2L);
                Township township121 = Township.find.byId(6L);
                Township township122 = Township.find.byId(7L);
                Village village1111 = Village.find.byId(1L);
                Village village1112 = Village.find.byId(2L);
                Village village1121 = Village.find.byId(6L);
                Village village1122 = Village.find.byId(7L);
                Village village1211 = Village.find.byId(26L);
                Village village1212 = Village.find.byId(27L);
                Village village1221 = Village.find.byId(31L);
                Village village1222 = Village.find.byId(32L);
                Region region2 = Region.find.byId(2L);
                District district21 = District.find.byId(6L);
                District district22 = District.find.byId(7L);
                Township township211 = Township.find.byId(26L);
                Township township212 = Township.find.byId(27L);
                Township township221 = Township.find.byId(31L);
                Township township222 = Township.find.byId(32L);
                Village village2111 = Village.find.byId(126L);
                Village village2112 = Village.find.byId(127L);
                Village village2121 = Village.find.byId(131L);
                Village village2122 = Village.find.byId(132L);
                Village village2211 = Village.find.byId(151L);
                Village village2212 = Village.find.byId(152L);
                Village village2221 = Village.find.byId(156L);
                Village village2222 = Village.find.byId(157L);

                ShippingCity.seed(region1, district11, township111, village1111, user);
                ShippingCity.seed(region1, district11, township111, village1112, user);
                ShippingCity.seed(region1, district11, township112, village1121, user);
                ShippingCity.seed(region1, district11, township112, village1122, user);
                ShippingCity.seed(region1, district12, township121, village1211, user);
                ShippingCity.seed(region1, district12, township121, village1212, user);
                ShippingCity.seed(region1, district12, township122, village1221, user);
                ShippingCity.seed(region1, district12, township122, village1222, user);


                ShippingCity.seed(region2, district21, township211, village2111, user);
                ShippingCity.seed(region2, district21, township211, village2112, user);
                ShippingCity.seed(region2, district21, township212, village2121, user);
                ShippingCity.seed(region2, district21, township212, village2122, user);
                ShippingCity.seed(region2, district22, township221, village2211, user);
                ShippingCity.seed(region2, district22, township221, village2212, user);
                ShippingCity.seed(region2, district22, township222, village2221, user);
                ShippingCity.seed(region2, district22, township222, village2222, user);
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingCourier() {
        if (Courier.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms user = UserCms.find.byId(1L);
//                Courier.seed("Go Send", CourierType.courierTypeVolumetric.getId(), 2D, "courier/kurir-gosend.png", user, 2, 47);
//                Courier.seed("J&T", CourierType.courierTypeWeight.getId(), 0D, "courier/kurir-jnt.png", user, 3, 48);
//                Courier.seed("POS Indonesia", CourierType.courierTypeVolume.getId(), 0D, "courier/kurir-pos.png", user, 4, 49);
                Courier.seed2("TIKI", "tiki", "courier/kurir-gosend.png", user);
                Courier.seed2("JNE", "jne", "courier/kurir-jnt.png", user);
                Courier.seed2("POS Indonesia", "pos", "courier/kurir-pos.png", user);
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }

        if (ShippingCost.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms user = UserCms.find.byId(1L);

                List<Township> townships = Township.find.all();
                int length = townships.size();
                Courier.find.all().forEach(c->{
                    for (int i=0; i<length; i++){
                        Township t1 = townships.get(i);
                        for (int j=i; j<length; j++){
                            Township t2 = townships.get(j);
                            ShippingCost.seed(c, t1, t2, user);
                        }
                    }
                });
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }


    static void seedTestingPaymentExpiration() {
        if (PaymentExpiration.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms user = UserCms.find.byId(1L);
                PaymentExpiration.seed("hour", 48, true, user);
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingOrder() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        if (SalesOrder.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                List<Long> products = new ArrayList<>();
                products.add(1L);
                products.add(9L);
                SalesOrder.seed("SO"+(simpleDateFormat.format(new Date()))+"00001",SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION, products);

                List<Long> products2 = new ArrayList<>();
                products2.add(9L);
                products2.add(2L);
                SalesOrder.seed("SO"+(simpleDateFormat.format(new Date()))+"00002",SalesOrder.ORDER_STATUS_VERIFY, products2);
                SalesOrder.seed("SO"+(simpleDateFormat.format(new Date()))+"00003",SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT, products2);

                SalesOrderReturn sr = new SalesOrderReturn();
                sr.requestAt = new Date();
                sr.status = SalesOrderReturn.STATUS_PENDING;
                sr.description = "Not working";
                sr.salesOrder = SalesOrder.find.byId(3L);
                sr.member = sr.salesOrder.member;
                sr.returnNumber = SalesOrderPayment.find.byId(1L).invoiceNo;
                sr.save();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingVoucher() {
        if (Voucher.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Date date1 = new SimpleDateFormat( "yyyyMMdd" ).parse( "20170501" );
                Date date2 = new SimpleDateFormat( "yyyyMMdd" ).parse( "20170520" );
                Date date3 = new SimpleDateFormat( "yyyyMMdd" ).parse( "20170521" );
                Date date4 = new SimpleDateFormat( "yyyyMMdd" ).parse( "20170531" );
                UserCms user = UserCms.find.byId(1L);
                Voucher.seed("VOUCHER1", "DISKON10", Voucher.TYPE_DISCOUNT, 10D, 2, 12, 100000D, 1000000D, 1, 1, date1, date2, Voucher.FILTER_STATUS_ALL, Voucher.ASSIGNED_TO_ALL, user);
                Voucher.seed("VOUCHER2", "", Voucher.TYPE_FREE_DELIVERY, 0D, 0, 5, 500000D, 400000D, 1, 1, date3, date4, Voucher.FILTER_STATUS_ALL, Voucher.ASSIGNED_TO_ALL, user);
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingParam() {
        if (Param.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Param productMarketplaceRejectInfo1 = new Param("product-marketplace-reject-info", "1","Image Broken");
                Param productMarketplaceRejectInfo2 = new Param("product-marketplace-reject-info", "2","Short Description Not Filled");
                Param productMarketplaceRejectInfo3 = new Param("product-marketplace-reject-info", "3","Long Description Not Filled");
                Param productMarketplaceRejectInfo4 = new Param("product-marketplace-reject-info", "4","What's In The Box Not Filled");
                Param productMarketplaceRejectInfo5 = new Param("product-marketplace-reject-info", "5","Wrong Category");
                Param productMarketplaceRejectInfo6 = new Param("product-marketplace-reject-info", "6","Mistake Product Name");
                Param productMarketplaceRejectInfo7 = new Param("product-marketplace-reject-info", "7","Irrelevant Product");
                Param productMarketplaceRejectInfo8 = new Param("product-marketplace-reject-info", "8","Others");
                productMarketplaceRejectInfo1.save();
                productMarketplaceRejectInfo2.save();
                productMarketplaceRejectInfo3.save();
                productMarketplaceRejectInfo4.save();
                productMarketplaceRejectInfo5.save();
                productMarketplaceRejectInfo6.save();
                productMarketplaceRejectInfo7.save();
                productMarketplaceRejectInfo8.save();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
    }

    static void seedTestingPurchaseOrder() {
        if (PurchaseOrder.find.findRowCount() == 0) {
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms user = UserCms.find.byId(1L);
//                Vendor vendor = Vendor.find.byId(1L);
//                List<Long> products = new ArrayList<>();
//                products.add(8L);
//                products.add(9L);
//                products.add(10L);
//                PurchaseOrder.seed("PO2017000001", vendor, 2, products, user);
//                PurchaseOrder.seed("PO2017000002", vendor, 4, products, user);

                List<Product> all = Product.find.where().eq("product_type", 1).findList();
                Map<Vendor, List<Product>> mapVendor = new HashMap<>();
                all.forEach(p->{
                    Vendor vendor = p.vendor;
                    List<Product> ps = new ArrayList<Product>();
                    if (mapVendor.containsKey(vendor)){
                        ps = mapVendor.get(vendor);
                    }
                    ps.add(p);
                    mapVendor.put(vendor, ps);
                });

                mapVendor.forEach((k,v)-> PurchaseOrder.seed(k, PurchaseOrder.SENT, v, user));
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }

//        if (PurchaseOrderReturn.find.findRowCount() == 0) {
//            Transaction txn = Ebean.beginTransaction();
//            try {
//                UserCms user = UserCms.find.byId(1L);
//                Vendor vendor = Vendor.find.byId(1L);
//                List<Long> products = new ArrayList<>();
//                products.add(8L);
//                products.add(9L);
//                products.add(10L);
//                PurchaseOrderReturn.seed("PR2017000001", vendor, "F", "A", products, user);
//                PurchaseOrderReturn.seed("PR2017000002", vendor, "R", "C", products, user);
//                PurchaseOrderReturn.seed("PR2017000003", vendor, "R", "P", products, user);
//                txn.commit();
//            }catch (Exception e) {
//                e.printStackTrace();
//                txn.rollback();
//            } finally {
//                txn.end();
//            }
//        }
    }

}
