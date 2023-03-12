_Update 12/03/2023:_ As the COVID-19 pandemic is mostly over, this bot has been sun-downed. Thanks for your support!

# CovidImpfDeBot

A [Telegram Bot](https://t.me/CovidImpfDEBot) with that informs about the current progress in the German COVID-19 vaccination campaign. Includes a (simple) daily estimation when herd immunity will be reached. Users can subscribe to get notified about vaccination and delivery updates. 

![Screenshot of /impf command example result](https://github.com/PhillipHow/CovidImpfDeBot/blob/ce76f651b290ac2d5d4c1c685071a7ab6b807c18/command-example.png)

Written in Java 8 with a strong focus on DRY and the SRP. Published under the MIT license. Vaccination data is fetched from the [Impfdashboard of the german Robert Koch institute](https://impfdashboard.de/).

Thanks for 325 subscribed bot users! :)

## Milestone date calculation

The algorithm for calculating the milestone dates works as follows: 

```pseudocode
movingAverageDoses = (average of first doses issued per day during last 14 days);
firstDosesIssued = (how many peole have received their first dose);
GERMAN_POPULATION = 83157201
POPULATION_QUOTA_VACCINATED = 0.5 | 0.6 | 0.7 | 0.8 | 0.9

peopleThatStillNeedToBeVaccinated = (GERMAN_POPULATION * POPULATION_QUOTA_VACCINATED) - firstDosesIssued;
daysNeeded = peopleThatStillNeedToBeVaccinated / movingAverageDoses; 

return Date.today().plusDays(daysNeeded);
```

The code can be found in [VaccinationDataInterpretation.java](https://github.com/PhillipHow/CovidImpfDeBot/blob/master/src/main/java/de/philliphow/covidimpfde/logic/VaccinationDataInterpretation.java). 

As you can see, this algorithm only takes first doses into account. This decision was made because in times of big enough vaccine deliveries, the distribution of the second dose is less a matter of vaccination rates and more one of constant time (e.g. 4-6 Weeks for the Biontech/Pfizer vaccine). 



## For end users

Just open https://t.me/CovidImpfDEBot and click the "Start" button in your telegram client. You can also invite the bot to group chats and use it from there. 

## Own build

Be sure to have Maven (I used 3.5.0) and Java 8 installed. 

1. Clone the repository. 
2. Run `mvn clean verify` in the project root. 
3. Switch to the `target` folder. 
4. You can now start the bot with `java -jar covidimpfde-...-with-dependencies.jar (prod | debug) <BotUsername> <BotToken> [<AdminChatId>]` 
   - If you use `prod`, the bot will get the data from the online Impfdashboard api. If you use `debug`, it will refresh the data more often (every 30 seconds) and get it from two local files (useful for debugging):
     - Delivery data: `test-datasets/debug_delivery_timeseries.tsv`
     - Vaccination data: `test-datasets/debug_vaccination_timeseries.tsv`
   - `BotUsername` and `BotToken`: Obtain these via the [BotFather](https://t.me/BotFather) of the telegram bots api. 
   - `AdminChatId` (optional): Telegram chat id to be notified on important bot events (if exception occur or updates are send out). You can obtain the chat id by subscribing to the bot yourself and looking up your chat id in the `subs` file. 

## Contributing

Please star this repository if the daily updates helped you through the last weeks of lockdown (like they did for me). Feel free to open pull requests in german or english if you notice bugs or typos! :) Pull requests with new features are also welcome, but keep in mind that the bot should stay rather lightweight and simple. 

## Thanks

... to rubenlagus for his [Telegram Bots API library](https://github.com/rubenlagus/TelegramBots), I've been a big fan over the last years!









