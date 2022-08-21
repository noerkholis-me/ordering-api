
import akka.actor.Cancellable;
import assets.Tool;
import com.hokeba.scheduler.DailyJob;
import com.hokeba.scheduler.ElasticSearchServiceJob;
import com.hokeba.scheduler.GoogleCatalogJob;
import com.hokeba.scheduler.OrderDeliverCheckJob;
import com.hokeba.scheduler.ServiceJob;
import com.hokeba.scheduler.WeeklySettlementJob;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Global extends GlobalSettings {
	private List<Cancellable> jobs = new ArrayList<>(1);

	private class ActionWrapper extends Action.Simple {
		public ActionWrapper(Action<?> action) {
			this.delegate = action;
		}

		@Override
		public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
			Promise<Result> result = this.delegate.call(ctx);
			Http.Request request = ctx.request();
			Http.Response response = ctx.response();
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, " +
					"X-Requested-With, X-API_KEY, x-api_key, API_KEY, api_key, Api-Key," +
					"X-Token, x-token, Token, token, X-API-KEY, X-API-TOKEN");
			if (request.path().startsWith("/images/")) {
				response.setHeader("Cache-Control", "max-age=180, no-transform");
			}
			return result;
		}
	}

	@Override
	public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
		Logger.info("\nREQUEST [" + request.method() + "] " + request.host() + request.uri());
		if (request.body().asJson() != null) {
			Logger.info(Tool.prettyPrint(request.body().asJson()));
		}
		Logger.info(new Date() + "\n");
		return new ActionWrapper(super.onRequest(request, actionMethod));
	}

	@Override
	public void onStart(Application app) {
		super.onStart(app);
		Runnable seeder = new SeedRunnable();
		Thread seederThread = new Thread(seeder);
		seederThread.setPriority(Thread.MAX_PRIORITY);
		seederThread.start();

	}

	@Override
	public void onStop(Application app)
	{
		Logger.info("Global.onStop...");
		Logger.info("Jobs Cancellable.");
		for (Cancellable sch : jobs) {
			sch.cancel();
		}

		super.onStop(app);
	}
	public class SeedRunnable implements Runnable {
		@Override
		public void run() {
			Logger.info("SeedRunnable.run...");
			try {
//				Logger.info("Seeding Features...");
//				SeedDefaultConfiguration.seedFeature();
//				Logger.info("Seeding Features done.\nSeeding User...");
//				SeedDefaultConfiguration.seedUser();
//				Logger.info("Seeding users done.\nSeeding Configuration...");
//				SeedDefaultConfiguration.seedConfigSetting();
//				Logger.info("Seeding config setting done.\n Seeding Payment Expiration...");
//				SeedDefaultConfiguration.seedPaymentExpiration();
//				Logger.info("Seeding payment expiration done.\nSeeding District, Township and Village...");
//				SeedDefaultConfiguration.seedRegionDistrictTownshipVillage();
//				Logger.info("Seeding region done.\nSeeding Loyalty...");
//				SeedDefaultConfiguration.seedLoyaltyPageBanner();
//				Logger.info("Seeding loyalty done.\nSeeding Currency...");
//				SeedDefaultConfiguration.seedCurrency();
//				Logger.info("Seeding currency done.\nSeeding Mobile version...");
//				SeedDefaultConfiguration.seedMobileVersion();
//				Logger.info("Seeding mobile version done.\nSeeding Region District From RajaOngkir...");
//				SeedDefaultConfiguration.seedRegionDistrictFromRajaOngkir();
//				Logger.info("Seeding region district from rajaongkir done.\nSeeding Courier...");
//				SeedDefaultConfiguration.seedCourier();
//				Logger.info("Seeding courier done.\nSeeding Own Merchant...");
//				SeedDefaultConfiguration.seedOwnMerchant();
//				Logger.info("Seeding own merchant done.\nSeeding master variance...");
//				SeedDefaultConfiguration.seedMasterVariance();
//				Logger.info("Seeding master variance done.\nSeeding Shipper...");
//				SeedDefaultConfiguration.seedSyncShipper();
//				Logger.info("Seeding shipper done.\n");
				//        SynchronizeController.syncSeed();

				//		jobs.add(new ServiceJob("* * * * * ?").scheduleIntervalMinutes());

				//		jobs.add(new ElasticSearchServiceJob("*/30 * * * * ?").scheduleIntervalMinutes(30));

				//		jobs.add(new DailyJob("0 0 1 * * ?").scheduleInterval());
				//		jobs.add(new GoogleCatalogJob("0 0 2 * * ?").scheduleInterval());

				//		jobs.add(new WeeklySettlementJob("0 0 3 ? * MON,WED,FRI").scheduleInterval());
				//		jobs.add(new OrderDeliverCheckJob("0 0 2 * * ?").scheduleInterval());
			} catch (Exception ex) {
				System.out.println("[ERROR] SeedDefaultConfiguration: " + ex.getMessage());
			}
		}
	}

}
