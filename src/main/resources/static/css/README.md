
# CSS da parte publica

O css da parte publica é gerado a partir dos arquivos scss.


### Build

Instale o compilador SCSS

    npm install -g sass

Entre no diretório scss e compine os arquivos

    cd scss/ssr
    sass ./public-style.scss ./public-style.min.css --style compressed


Copie os arquivos **public-style.min.css** e **public-style.min.css.map** para esse diretório.