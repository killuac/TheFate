//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsPingRequest.h"
#import "EsThriftUtil.h"

@implementation EsPingRequest

@synthesize globalResponseRequested = globalResponseRequested_;
@synthesize sessionKey = sessionKey_;
@synthesize pingRequestId = pingRequestId_;

- (id) initWithThriftObject: (id) thriftObject {
	if ((self = [super init])) {
		self.messageType = EsMessageType_PingRequest;
		if (thriftObject != nil) {
			[self fromThrift: thriftObject];
		}
	}
	return self;
}

- (id) init {
	return [self initWithThriftObject: nil];
}

- (void) fromThrift: (id) thriftObject {
	ThriftPingRequest* t = (ThriftPingRequest*) thriftObject;
	if ([t globalResponseRequestedIsSet]) {
		self.globalResponseRequested = t.globalResponseRequested;
	}
	if ([t sessionKeyIsSet]) {
		self.sessionKey = t.sessionKey;
	}
	if ([t pingRequestIdIsSet]) {
		self.pingRequestId = t.pingRequestId;
	}
}

- (ThriftPingRequest*) toThrift {
	ThriftPingRequest* _t = [[ThriftPingRequest alloc] init];;
	if (globalResponseRequested_set_) {
		BOOL _globalResponseRequested;
		_globalResponseRequested = self.globalResponseRequested;
		_t.globalResponseRequested = _globalResponseRequested;
	}
	if (sessionKey_set_) {
		int32_t _sessionKey;
		_sessionKey = self.sessionKey;
		_t.sessionKey = _sessionKey;
	}
	if (pingRequestId_set_) {
		int32_t _pingRequestId;
		_pingRequestId = self.pingRequestId;
		_t.pingRequestId = _pingRequestId;
	}
	return _t;
}

- (id) newThrift {
	return [[ThriftPingRequest alloc] init];
}

- (void) setGlobalResponseRequested: (BOOL) globalResponseRequested {
	globalResponseRequested_ = globalResponseRequested;
	globalResponseRequested_set_ = true;
}

- (void) setSessionKey: (int32_t) sessionKey {
	sessionKey_ = sessionKey;
	sessionKey_set_ = true;
}

- (void) setPingRequestId: (int32_t) pingRequestId {
	pingRequestId_ = pingRequestId;
	pingRequestId_set_ = true;
}


@end
