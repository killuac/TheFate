//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsCreateRoomVariableRequest.h"
#import "EsThriftUtil.h"

@implementation EsCreateRoomVariableRequest

@synthesize zoneId = zoneId_;
@synthesize roomId = roomId_;
@synthesize name = name_;
@synthesize value = value_;
@synthesize locked = locked_;
@synthesize persistent = persistent_;

- (id) initWithThriftObject: (id) thriftObject {
	if ((self = [super init])) {
		self.messageType = EsMessageType_CreateRoomVariableRequest;
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
	ThriftCreateRoomVariableRequest* t = (ThriftCreateRoomVariableRequest*) thriftObject;
	if ([t zoneIdIsSet]) {
		self.zoneId = t.zoneId;
	}
	if ([t roomIdIsSet]) {
		self.roomId = t.roomId;
	}
	if ([t nameIsSet] && t.name != nil) {
		self.name = t.name;
	}
	if ([t valueIsSet] && t.value != nil) {
		self.value = [EsThriftUtil unflattenEsObject:[[EsFlattenedEsObject alloc] initWithThriftObject:t.value]];
	}
	if ([t lockedIsSet]) {
		self.locked = t.locked;
	}
	if ([t persistentIsSet]) {
		self.persistent = t.persistent;
	}
}

- (ThriftCreateRoomVariableRequest*) toThrift {
	ThriftCreateRoomVariableRequest* _t = [[ThriftCreateRoomVariableRequest alloc] init];;
	if (zoneId_set_) {
		int32_t _zoneId;
		_zoneId = self.zoneId;
		_t.zoneId = _zoneId;
	}
	if (roomId_set_) {
		int32_t _roomId;
		_roomId = self.roomId;
		_t.roomId = _roomId;
	}
	if (name_set_ && name_ != nil) {
		NSString* _name;
		_name = self.name;
		_t.name = _name;
	}
	if (value_set_ && value_ != nil) {
		ThriftFlattenedEsObject* _value;
		_value = [[EsThriftUtil flattenEsObject:self.value] toThrift];
		_t.value = _value;
	}
	if (locked_set_) {
		BOOL _locked;
		_locked = self.locked;
		_t.locked = _locked;
	}
	if (persistent_set_) {
		BOOL _persistent;
		_persistent = self.persistent;
		_t.persistent = _persistent;
	}
	return _t;
}

- (id) newThrift {
	return [[ThriftCreateRoomVariableRequest alloc] init];
}

- (void) setZoneId: (int32_t) zoneId {
	zoneId_ = zoneId;
	zoneId_set_ = true;
}

- (void) setRoomId: (int32_t) roomId {
	roomId_ = roomId;
	roomId_set_ = true;
}

- (void) setName: (NSString*) name {
	name_ = name;
	name_set_ = true;
}

- (void) setValue: (EsObject*) value {
	value_ = value;
	value_set_ = true;
}

- (void) setLocked: (BOOL) locked {
	locked_ = locked;
	locked_set_ = true;
}

- (void) setPersistent: (BOOL) persistent {
	persistent_ = persistent;
	persistent_set_ = true;
}

- (void) dealloc {
	self.value = nil;
}

@end
