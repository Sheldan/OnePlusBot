# r/oneplus Discord server bot

This repository contains the rewrite of the [Bot](https://github.com/Rithari/OnePlusBot). It is developed by customizing [abstracto](https://github.com/Sheldan/abstracto), because some features are r/oneplus specific.

The migration of the existing data from the database is handled via one time migration, and can be found [here](https://github.com/Sheldan/OnePlusBot-migration).

The FAQ configuration can be found [here](https://github.com/Sheldan/OnePlusBot-faq/).

Custom features which were ported
 - [x] FAQ
 - [x] Setup channel handling
 - [x] Referral link handling 

# Technologies used in addition to the ones provided in abstracto
- [grafana](https://github.com/grafana/grafana) for visualization of the bot status and metrics
- [Loki](https://github.com/grafana/loki) to visualize and query log files
- [pgAdmin](https://github.com/postgres/pgadmin4) to view the database
- [prometheus](https://github.com/prometheus/prometheus) for metric collection
- [postgres](https://github.com/postgres/postgres) as a database
- [loki](https://github.com/grafana/loki) for log aggregation
  