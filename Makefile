
.PHONY: prep
prep:
	if [ ! -d "themes/hello-friend" ]; then git clone --branch endot git@github.com:justone/hugo-theme-hello-friend.git themes/hello-friend; fi
	if [ ! -d "themes/hugo-atom-feed" ]; then git clone https://github.com/kaushalmodi/hugo-atom-feed.git themes/hugo-atom-feed; fi

.PHONY: site
site: prep
	rm -rf public
	hugo

.PHONY: dev-local
dev-local: prep
	hugo server -D --bind 0.0.0.0 --baseURL http://192.168.1.105/

.PHONY: dev-remote
dev-remote: prep
	hugo server -D --bind 0.0.0.0

.PHONY: push-staging
push-staging: prep
	rm -rf public
	./deploy.sh staging

.PHONY: push-prod
push-prod: prep
	rm -rf public
	./deploy.sh prod
