package io.github.adainish.wynautrankup;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.wynautrankup.arenas.ArenaConfig;
import io.github.adainish.wynautrankup.arenas.ArenaManager;
import io.github.adainish.wynautrankup.cmd.*;
import io.github.adainish.wynautrankup.config.GeneralConfig;
import io.github.adainish.wynautrankup.data.MatchMakingQueue;
import io.github.adainish.wynautrankup.database.DatabaseConfig;
import io.github.adainish.wynautrankup.database.PlayerDataManager;
import io.github.adainish.wynautrankup.database.DatabaseManager;
import io.github.adainish.wynautrankup.handler.BattleResultHandler;
import io.github.adainish.wynautrankup.playerlistener.PlayerEvents;
import io.github.adainish.wynautrankup.ranks.RankManager;
import io.github.adainish.wynautrankup.season.RewardScheduler;
import io.github.adainish.wynautrankup.season.SeasonManager;
import io.github.adainish.wynautrankup.shop.ShopManager;
import io.github.adainish.wynautrankup.util.AsyncExecutor;
import io.github.adainish.wynautrankup.util.TeamValidator;
import io.github.adainish.wynautrankup.validator.TeamValidationConfig;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;


public class WynautRankUp implements ModInitializer {
    public static WynautRankUp instance;
    public MinecraftServer server;
    public AsyncExecutor asyncExecutor = new AsyncExecutor(4);
    public PlayerDataManager playerDataManager;
    public MatchMakingQueue matchmakingQueue;
    public BattleResultHandler battleResultHandler;
    public TeamValidator teamValidator;
    public DatabaseConfig databaseConfig;
    public DatabaseManager databaseManager;
    public ArenaConfig arenaConfig;
    public ArenaManager arenaManager;
    public GeneralConfig generalConfig;
    public RankManager rankManager;
    public PlayerEvents playerEvents;
    public SeasonManager seasonManager;
    public ShopManager shopManager;
    public RewardScheduler rewardScheduler;
    public WynautRankUp() {
        instance = this;

    }
    @Override
    public void onInitialize() {
        PlatformEvents.SERVER_STARTED.subscribe(Priority.NORMAL, event -> {
            server = event.getServer();
            // Initialize the matchmaking queue
            load();
            return Unit.INSTANCE;
        });
        PlatformEvents.SERVER_STOPPING.subscribe(Priority.NORMAL, event -> {
            // Save player data and clean up resources
            shutdown();
            return Unit.INSTANCE;
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            //command registration
            dispatcher.register(PlayerCommand.getCommand());
            dispatcher.register(AdminCommand.getCommand());
            dispatcher.register(ArenaCommand.getCommand());
            dispatcher.register(SeasonCommand.getCommand());
            dispatcher.register(ShopCommand.getShopCommand());
        });
    }

    public void load()
    {
        //once config systems setup, load config here
        databaseConfig = DatabaseConfig.loadFromFile();
        this.databaseManager = new DatabaseManager();
        databaseManager.DATABASE_URL = databaseConfig.databaseUrl;

        TeamValidationConfig teamValidationConfig = new TeamValidationConfig();
        teamValidator = new TeamValidator();
        teamValidator.setConfig(teamValidationConfig);

        this.generalConfig = new GeneralConfig();
        this.generalConfig.load();
        this.arenaConfig = new ArenaConfig();
        this.arenaManager = new ArenaManager();
        this.arenaManager.reload();
        this.rankManager = new RankManager();

        this.matchmakingQueue =  new MatchMakingQueue();
        // Load player data
        this.playerDataManager = new PlayerDataManager(asyncExecutor);

        this.seasonManager = new SeasonManager();
        this.seasonManager.loadSeasons();
        this.seasonManager.setCurrentSeasonByActiveDate();

        this.shopManager = new ShopManager();
        this.shopManager.writeDefaultConfig();
        this.shopManager.loadFromConfig();

        this.rewardScheduler = new RewardScheduler();
        this.rewardScheduler.start();

        this.battleResultHandler = new BattleResultHandler();
        battleResultHandler.registerEventListeners();
        this.playerEvents = new PlayerEvents();


        System.out.println("Wynaut Rank Up Loaded");
    }

    public void shutdown()
    {
        // Save player data and clean up resources
        playerDataManager.saveAllPlayerData();
        matchmakingQueue.stopQueueProcessing();
        matchmakingQueue.clearQueue();
        matchmakingQueue.getRankedBattleTracker().clearRankedBattles();
        databaseManager.closeDatabase();
        asyncExecutor.shutdownExecutor();
    }

    public void reloadConfig() {
        TeamValidationConfig teamValidationConfig = new TeamValidationConfig();
        this.teamValidator = new TeamValidator();
        this.teamValidator.setConfig(teamValidationConfig);
        this.generalConfig.load();
        this.arenaManager.reload();
        this.rankManager = new RankManager();
    }
}
