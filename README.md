# CovidImpfDeBot

A telegram bot that informs about the current progress in the German COVID-19 vaccination campaign. Includes a (scientifically not very accurate) daily estimation when herd immunity will be reached. Users can subscribe to get notified about vaccination and delivery updates. 

Written in Java 8 with a strong focus on DRY and the SRP. Published under the MIT license. Vaccination data is fetched from the [Impfdashboard of the german Robert Koch institute](https://impfdashboard.de/).

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

Please star this repository if the daily updates helped you through the last weeks of lockdown (like they did for me). Feel free to open pull requests in german or english if you notice bugs or typos! :) Pull requests with new features are also welcome, but keep in mind that the bot should stay rather lightwight and simple. 

## Thanks

... to rubenlagus for his [Telegram Bots API library](https://github.com/rubenlagus/TelegramBots), I've been a big fan over the last years!









