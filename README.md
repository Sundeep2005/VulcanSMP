# VulcanSMP

Een uitgebreide core plugin voor Paper 1.21.1 Minecraft servers met MiniMessage ondersteuning, configureerbare instellingen, database ondersteuning en integratie met populaire plugins.

## Kenmerken

### Commando's

| Commando | Beschrijving | Permissie |
|----------|-------------|-----------|
| `/gamemode <mode>` | Verander gamemode | `vulcan.gamemode.<mode>` |
| `/gmc`, `/gms`, `/gma`, `/gmsp` | Snelle gamemode shortcuts | `vulcan.gamemode.<mode>` |
| `/fly [speler]` | Toggle vliegen | `vulcan.fly` |
| `/tp <speler> [naar]` | Teleporteer | `vulcan.teleport` |
| `/tpa <speler>` | Vraag teleportatie aan | `vulcan.tpa` |
| `/tpaccept` | Accepteer teleportatie | `vulcan.tpaccept` |
| `/tpadeny` | Weiger teleportatie | `vulcan.tpadeny` |
| `/tpall` | Teleporteer iedereen naar jou | `vulcan.tpall` |
| `/back` | Ga terug naar vorige locatie | `vulcan.back` |
| `/home [naam]` | Teleporteer naar home | `vulcan.home` |
| `/home set [naam]` | Stel home in | `vulcan.home.set` |
| `/home delete <naam>` | Verwijder home | `vulcan.home.delete` |
| `/home list` | Bekijk homes | `vulcan.home.list` |
| `/baltop [aantal]` | Bekijk rijkste spelers | `vulcan.baltop` |
| `/money pay <speler> <bedrag>` | Betaal een speler | `vulcan.money.pay` |
| `/money give <speler> <bedrag>` | Geef geld (admin) | `vulcan.money.give` |
| `/money take <speler> <bedrag>` | Neem geld af (admin) | `vulcan.money.take` |
| `/money set <speler> <bedrag>` | Stel balans in (admin) | `vulcan.money.set` |
| `/warp <naam>` | Teleporteer naar warp | `vulcan.warp` |
| `/warp list` | Bekijk warps | `vulcan.warp.list` |
| `/warp create <naam>` | Maak warp aan | `vulcan.warp.create` |
| `/warp delete <naam>` | Verwijder warp | `vulcan.warp.delete` |
| `/heal [speler]` | Genees speler | `vulcan.heal` |
| `/feed [speler]` | Voed speler | `vulcan.feed` |
| `/weather <type>` | Verander weer | `vulcan.weather` |
| `/spawn` | Teleporteer naar spawn | `vulcan.spawn` |
| `/setspawn [naam]` | Stel spawn in | `vulcan.setspawn` |
| `/removespawn <naam>` | Verwijder spawn | `vulcan.removespawn` |
| `/broadcast <bericht>` | Broadcast bericht | `vulcan.broadcast` |
| `/enderchest [speler]` | Open enderchest | `vulcan.enderchest` |
| `/invsee <speler>` | Bekijk inventaris | `vulcan.invsee` |
| `/kit <naam>` | Claim kit | `vulcan.kit` |
| `/kit create <naam>` | Maak kit van hotbar | `vulcan.kit.create` |
| `/kit delete <naam>` | Verwijder kit | `vulcan.kit.delete` |
| `/msg <speler> <bericht>` | Stuur privébericht | `vulcan.msg` |
| `/reply <bericht>` | Beantwoord bericht | `vulcan.reply` |
| `/playtime [speler]` | Bekijk speeltijd | `vulcan.playtime` |
| `/skull <naam>` | Krijg speler schedel | `vulcan.skull` |
| `/vanish` | Toggle onzichtbaarheid | `vulcan.vanish` |
| `/discord` | Toon Discord link | `vulcan.discord` |
| `/store` | Toon webshop link | `vulcan.store` |
| `/vote` | Toon stem link | `vulcan.vote` |
| `/friend add <speler>` | Stuur vriendschapsverzoek | `vulcan.friend` |
| `/friend remove <speler>` | Verwijder vriend | `vulcan.friend` |
| `/friend list` | Bekijk vrienden | `vulcan.friend` |

### Integraties

- **Vault** - Economie ondersteuning
- **PlaceholderAPI** - Custom placeholders
- **LuckPerms** - Permissie-gebaseerde home limieten
- **WorldGuard** - Regio bescherming checks
- **HeadDatabase** - Custom heads in `/skull` commando

### PlaceholderAPI Placeholders

| Placeholder | Beschrijving |
|-------------|-------------|
| `%vulcan_playtime%` | Geformatteerde speeltijd |
| `%vulcan_playtime_raw%` | Speeltijd in milliseconden |
| `%vulcan_vanished%` | Vanish status |
| `%vulcan_friends%` | Aantal vrienden |
| `%vulcan_homes%` | Aantal homes |
| `%vulcan_home_limit%` | Home limiet |
| `%vulcan_balance%` | Geformatteerde balans |
| `%vulcan_balance_raw%` | Ruwe balans |
| `%vulcan_is_staff%` | Staff status |
| `%vulcan_prefix%` | LuckPerms prefix |
| `%vulcan_suffix%` | LuckPerms suffix |
| `%vulcan_group%` | LuckPerms primaire groep |

## Configuratie

### Database

Ondersteunt SQLite (standaard) en MySQL:

```yaml
database:
  type: sqlite  # of mysql
  host: localhost
  port: 3306
  name: vulcansmp
  username: root
  password: ""
```

### Home Limieten

```yaml
homes:
  default-limit: 3
  limits:
    vip: 5
    mvp: 10
    premium: 20
```

Spelers hebben `vulcan.home.limit.<groep>` permissie nodig.

## Bouwen

```bash
./gradlew build
```

De plugin JAR staat in `build/libs/`.

## Vereisten

- Paper 1.21.1+
- Java 21+

## Optionele Dependencies

- Vault (voor economie)
- PlaceholderAPI (voor placeholders)
- LuckPerms (voor permissie integratie)
- WorldGuard (voor regio checks)
- HeadDatabase (voor custom heads)

## Licentie

MIT License
