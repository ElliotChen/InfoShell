# 從主機看世界

對於已經用了K8S之類，就....跳過這一篇吧

系統安裝時，特別是在Production，總有許多firewall的申請要處理，實際上線時，還有一堆要串接的系統要測試通或不通。

整個的可用性驗證可以分為幾個區塊

1. 物理上的可用性
2. 邏輯上的可用性
3. 安全上的可用性

但實際上沒有物理的可用性，其他的可用性就不用討論了。

## 假設場景

不知道大家有沒有遇過幾種情形，系統要上線時，發現要連的其他系統連不上，所以會用個人的電腦去試著連結，不管連不連的上，
還是會用各種方式在主機上去測，像用telnet去測ip,port是不是連得到，curl測restful的api，DB tool去測DB連不連得上。

或是莫明其妙的系統部份失效了，看得到Log也大約知道可能的問題，但還是希望能驗證是不是真的有問題。

如果有遇過的話，可以想想現在是怎麼處理這些情形的，還是手動一個一個測嗎？

不從主機往外看，看到的你相信嗎？

平行世界，指的就是個人電腦跟主機運作的方式吧。

## 還是寫程式吧

為了方便從主機看出去，還是寫程式吧。

把要測的東西用程式寫下來，無論是ip port的檢查，DB連結，api或是其他會用到的，都寫下來吧，即使是回應錯誤，最少知道他沒有完全抛下你。
只是給錯了東西，有救的；完全沒有回應，才是我們要擔心的。

世上最傷人的，其實是冷漠、不理不應，不是嗎？

## Spring Boot + Shell

Spring Boot 有幾個優點，預設會把需要的jar檔包在一起，最後只要丟一個jar就好，而且簡化了Spring的設定與使用，少了許多設定上的麻煩。
Spring Shell 則可以提供一個類似Console的介面，對於只是要簡單執行幾個測試的情景，再方使不過了。

Spring Boot 裡，說明這不是一個web application
```
spring:
  main:
    web-application-type: none
```

加上```@ShellComponent```跟```@ShellMethod```，把要執行的method列在console裡。

執行Spring Boot 像下列一樣，順使可以指定不同的環境

```
java -jar infoshell-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev 
```

大致可以看到下面的畫面，結尾的部份出現了```shell:>```的提示，等待我們的輸入

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.1.RELEASE)
...
2019-11-15 10:12:50.746  INFO 4506 --- [           main] tw.elliot.cli.CliApplication             : Started CliApplication in 1.596 seconds (JVM running for 2.21)
shell:>
```

輸入```help```可以取得當前可用的操作

```
shell:>help
AVAILABLE COMMANDS

Built-In Commands
        clear: Clear the shell screen.
        exit, quit: Exit the shell.
        help: Display help about available commands.
        history: Display or save the history of previously run commands
        script: Read and execute commands from a file.
        stacktrace: Display the full stacktrace of the last error.

Check System Status
        all: Check All Status
        database: Check DB Status
        info: Show Environment Info
        network: Check Network Status
        services: Check Service/API(Restful) Status
```

而```Check System Status```下列出的就是自行寫出的Command，然後再輸入要測的項目，如```network```

```
shell:>network
2019-11-15 10:50:39.959  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : Start to check Network
2019-11-15 10:50:39.959  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : Check ip:port [127.0.0.1:8082]
2019-11-15 10:50:39.961 ERROR 4578 --- [           main] tw.elliot.cli.util.SocketUtils           : Connect [127.0.0.1:8082] Failed, cause [Connection refused] 
2019-11-15 10:50:39.961  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : [127.0.0.1:8082] isn't available.
2019-11-15 10:50:39.961  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : Check ip:port [192.168.0.1:80]
2019-11-15 10:50:42.962 ERROR 4578 --- [           main] tw.elliot.cli.util.SocketUtils           : Connect [192.168.0.1:80] Failed, cause [Connect timed out] 
2019-11-15 10:50:42.962  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : [192.168.0.1:80] isn't available.
2019-11-15 10:50:42.962  INFO 4578 --- [           main] tw.elliot.cli.cmd.CheckSystemStatus      : End of check Network
```

## 窗

從你的窗看到的會是什麼樣的景色？
還是寫程式吧。
想像與憶測，都不是我們應該做的。

本篇的源始碼 [Spring Shell Demo](https://github.com/ElliotChen/InfoShell)

我敲了help，但卻沒有按下Enter.