
.PHONY: prep
prep:
	if [ ! -d "themes/hello-friend" ]; then git clone --branch endot git@github.com:justone/hugo-theme-hello-friend.git themes/hello-friend; fi

.PHONY: site
site: prep
	hugo

.PHONY: dev-local
dev-local: prep
	hugo server -D --bind 0.0.0.0 --baseURL http://192.168.1.105/

.PHONY: dev-remote
dev-remote: prep
	hugo server -D --bind 0.0.0.0
