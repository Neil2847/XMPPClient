# XMPPClient

## 說明
該端是Client端，若要建置Server端可以使用[Openfire](https://www.igniterealtime.org/projects/openfire/)這裡面來建置，若是要GUI介面可以使用**Spark**這個通訊窗口來做測試與驗證。

該Simple主要為連線、登入、登出、傳送訊息、接收訊息這幾項來撰寫，該專案主要是為了大家快速入門與了解。

<br>

## 提醒
> 以下有幾項比較重要的提醒這樣就可以避免不必消耗的時間。

- 連線不代表登入：連線只需要HOST & SeverName 對了自然就可以連線(但不代表登入)
- 要與其他帳號傳送訊息：需要注意的是 account@host 在用Message格式傳出即可
- 在背景保持連線：若是要在背景也保持連線狀態，則需要去製作一個Service埋入監聽
