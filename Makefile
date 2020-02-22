
.PHONY: prep
prep:
	if [ ! -d "themes/hello-friend" ]; then git clone git@github.com:justone/hugo-theme-hello-friend.git themes/hello-friend; fi

.PHONY: site
site: prep
	hugo

.PHONY: dev
dev: prep
	hugo server -D --bind 0.0.0.0 --baseURL http://192.168.1.105/
