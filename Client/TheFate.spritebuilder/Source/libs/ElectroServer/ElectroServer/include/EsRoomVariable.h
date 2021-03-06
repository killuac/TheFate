//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsMessage.h"
#import "EsMessageType.h"
#import "EsRequest.h"
#import "EsResponse.h"
#import "EsEvent.h"
#import "EsEntity.h"
#import "EsObject.h"
#import "ThriftRoomVariable.h"
#import "EsFlattenedEsObject.h"
#import "ThriftFlattenedEsObject.h"

@interface EsRoomVariable : EsEntity {
@private
	BOOL persistent_set_;
	BOOL persistent_;
	BOOL name_set_;
	NSString* name_;
	BOOL value_set_;
	EsObject* value_;
	BOOL locked_set_;
	BOOL locked_;
}

@property(nonatomic) BOOL persistent;
@property(strong,nonatomic) NSString* name;
@property(strong,nonatomic) EsObject* value;
@property(nonatomic) BOOL locked;

- (id) init;
- (id) initWithThriftObject: (id) thriftObject;
- (void) fromThrift: (id) thriftObject;
- (ThriftRoomVariable*) toThrift;
- (id) newThrift;
@end
