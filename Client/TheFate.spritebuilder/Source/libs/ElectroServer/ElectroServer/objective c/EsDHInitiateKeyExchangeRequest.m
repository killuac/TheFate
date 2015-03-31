//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsDHInitiateKeyExchangeRequest.h"
#import "EsThriftUtil.h"

@implementation EsDHInitiateKeyExchangeRequest


- (id) initWithThriftObject: (id) thriftObject {
	if ((self = [super init])) {
		self.messageType = EsMessageType_DHInitiate;
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
}

- (ThriftDHInitiateKeyExchangeRequest*) toThrift {
	ThriftDHInitiateKeyExchangeRequest* _t = [[ThriftDHInitiateKeyExchangeRequest alloc] init];;
	return _t;
}

- (id) newThrift {
	return [[ThriftDHInitiateKeyExchangeRequest alloc] init];
}


@end