# CovidImpfDeBot

A [Telegram Bot](https://t.me/CovidImpfDEBot) with that informs about the current progress in the German COVID-19 vaccination campaign. Includes a (simple) daily estimation when herd immunity will be reached. Users can subscribe to get notified about vaccination and delivery updates. 

Written in Java 8 with a strong focus on DRY and the SRP. Published under the MIT license. Vaccination data is fetched from the [Impfdashboard of the german Robert Koch institute](https://impfdashboard.de/).

Thanks for 325 subscribed bot users! :)

## Herd immunity date calculation

The algorithm for calculating the herd immunity date works as follows: 

```pseudocode
movingAverageDoses = (average of first doses issued per day during last 14 days);
firstDosesIssued = (how many peole have received their first dose);
GERMAN_POPULATION = 83157201
HERD_IMMUNITY_FAC = 0.6 or 0.8

peopleThatStillNeedToBeVaccinated = (GERMAN_POPULATION * HERD_IMMUNITY_FAC) - 			firstDosesIssued;
daysNeeded = peopleThatStillNeedToBeVaccinated / movingAverageDoses; 

return Date.today().plusDays(daysNeeded);
```

The code can be found in [VaccinationDataInterpretation.java](../src/main/java/de/philliphow/covidimpfde/logic/VaccinationDataInterpretation.java). 

As you can see, this (fairly simple) algorithm only takes first doses into account. This decision was made for several reasons. First of all, far more first than second doses are currently being distributed - this makes it difficult to calculate the date when a large proportion of the population has received both doses (at least based on current vaccination rates). In addition, the current evidence seems to be indicating that one shot already provides sufficient protection. It is therefore to be expected that e.g. contact restrictions can already be relaxed when a large part of the population has received their first dose - this makes the date of first dose herd immunity more interesting. 

Moreover, unlike the distribution of the first dose, the distribution of the second dose is less a matter of vaccination rates and more one of constant time.  As vaccine deliveries steadily increase in size, it is expected that everyone vaccinated will receive their second dose after 12 weeks at the latest. Thus, if no shortage of doses occurs, the date for full herd immunity can be assumed to be 12 weeks after first-dose herd immunity is achieved.



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









