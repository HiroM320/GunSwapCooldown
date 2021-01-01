# GunSwapCooldown

GunSwapCooldown is an addon for CrackShot.

---

## Introduction

Java初心者の時、友人からの依頼で作ったプラグインをバージョン1.12.2に向けに移植したものです。
CrackShotで作成した武器をメインハンドにもった直後、設定した時間だけ撃てなくなります。
よくあるFPSの武器切り替え時間のようなものです。

## Commands

コマンドの説明をします。

`/gsc help` -- コマンドの説明を表示します。

そのうち書きます。しばらくはゲーム内で`/gsc help`と入力して説明読んでください。

## Config

config.ymlの各項目を説明します。

```yaml:config.yml
Sound: ENTITY_PLAYER_LEVELUP
Volume: 1.0
Pitch: 1.0

DefaultCooldown: 10

Groups:
  AR: 10

AR:
  - AK-47
```

### Sound

クールダウン中に射撃しようとした時、再生する音を設定します。
例では`ENTITY_PLAYER_LEVELUP`、プレイヤーがレベルアップしたときに流れる音を再生します。

### Volume

音量を設定します。
例では`1`です。

### Pitch

ピッチを設定します。
例では`1`です。

### Groups

グループ名とグループ事のクールダウン時間をtick単位で設定します。
例ではグループ名が`AR`、クールダウン時間が`10`ticksになります。

### AR

例ではグループに`AR`を登録しているので、`AR:`と記述しています。
`AK-47`が`AR`のリストに記述されているので、この場合はグループ`AR`に`AK-47`が属していることが分かります。
