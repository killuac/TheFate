//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsAddBuddiesResponse.h"
#import "EsThriftUtil.h"

@implementation EsAddBuddiesResponse

@synthesize buddiesAdded = buddiesAdded_;
@synthesize buddiesNotAdded = buddiesNotAdded_;

- (id) initWithThriftObject: (id) thriftObject {
	if ((self = [super init])) {
		self.messageType = EsMessageType_AddBuddiesResponse;
		self.buddiesAdded = [NSMutableArray array];
		self.buddiesNotAdded = [NSMutableArray array];
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
	ThriftAddBuddiesResponse* t = (ThriftAddBuddiesResponse*) thriftObject;
	if ([t buddiesAddedIsSet] && t.buddiesAdded != nil) {
		self.buddiesAdded = [[NSMutableArray alloc] init];
		for (NSString* _tValVar_0 in t.buddiesAdded) {
			NSString* _listDestVar_0;
			_listDestVar_0 = _tValVar_0;
			[self.buddiesAdded addObject: _listDestVar_0];
		}
	}
	if ([t buddiesNotAddedIsSet] && t.buddiesNotAdded != nil) {
		self.buddiesNotAdded = [[NSMutableArray alloc] init];
		for (NSString* _tValVar_0 in t.buddiesNotAdded) {
			NSString* _listDestVar_0;
			_listDestVar_0 = _tValVar_0;
			[self.buddiesNotAdded addObject: _listDestVar_0];
		}
	}
}

- (ThriftAddBuddiesResponse*) toThrift {
	ThriftAddBuddiesResponse* _t = [[ThriftAddBuddiesResponse alloc] init];;
	if (buddiesAdded_set_ && buddiesAdded_ != nil) {
		NSMutableArray* _buddiesAdded;
		_buddiesAdded = [[NSMutableArray alloc] init];
		for (NSString* _tValVar_0 in self.buddiesAdded) {
			NSString* _listDestVar_0;
			_listDestVar_0 = _tValVar_0;
			[_buddiesAdded addObject: _listDestVar_0];
		}
		_t.buddiesAdded = _buddiesAdded;
	}
	if (buddiesNotAdded_set_ && buddiesNotAdded_ != nil) {
		NSMutableArray* _buddiesNotAdded;
		_buddiesNotAdded = [[NSMutableArray alloc] init];
		for (NSString* _tValVar_0 in self.buddiesNotAdded) {
			NSString* _listDestVar_0;
			_listDestVar_0 = _tValVar_0;
			[_buddiesNotAdded addObject: _listDestVar_0];
		}
		_t.buddiesNotAdded = _buddiesNotAdded;
	}
	return _t;
}

- (id) newThrift {
	return [[ThriftAddBuddiesResponse alloc] init];
}

- (void) setBuddiesAdded: (NSMutableArray*) buddiesAdded {
	buddiesAdded_ = buddiesAdded;
	buddiesAdded_set_ = true;
}

- (void) setBuddiesNotAdded: (NSMutableArray*) buddiesNotAdded {
	buddiesNotAdded_ = buddiesNotAdded;
	buddiesNotAdded_set_ = true;
}

- (void) dealloc {
	self.buddiesAdded = nil;
	self.buddiesNotAdded = nil;
}

@end
