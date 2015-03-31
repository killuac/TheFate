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
#import "ThriftCreateRoomRequest.h"
#import "EsFlattenedEsObject.h"
#import "ThriftFlattenedEsObject.h"
#import "EsPluginListEntry.h"
#import "ThriftPluginListEntry.h"
#import "EsRoomVariable.h"
#import "ThriftRoomVariable.h"

/**
 * This class represents a highly flexible server request. With it you can create a room, join that room if it already exists, set properties on that room (if it is being created), and set your own subscription properties for that room/zone.
 <br><br>Things to know about creating and joining rooms:<ul><li>If you create a new room you are automatically joined to that room. You cannot create a room without also being joined to that room.</li> 
 <li>When a room is created it is always created in a zone. If the zone name specified does not currently exist then it is created. Zones have no properties other than its name on the room list.</li>
 <li>When you join a room via the CreateRoomRequest, the JoinRoomRequest, or by some other method (such as joining a game or through the work of a server extension) you have the opportunity to subscribe to certain events. Please see below for more information on this.</li>
 <li>When creating a room you have the opportunity to set many initial properties such as its password, description, and inital room variables. All of these can be changed later.</li>
 <li>Rooms can be hidden. A hidden room does not show up in the room list. When a room is flagged as not hidden (which is the default behavior), then users see it appear in the list as if it were just added. When a room is flagged as hidden, users see the room removed as if it just died. Users in that hidden room see their own room.</li>
 </ul>
 <br>When joining a room there are many events that you are automatically subscribed to, unless you opt out. <br><br>Subscription properties when joining a room (all of the following default to true):
 <ul><li>receivingRoomListUpdates - If true, you receive ZoneUpdateEvent events when rooms are added to or removed from this zone, or when the the total count of users in one of those rooms changes.</li>
 <li>receivingUserListUpdates - If true, you receive RoomUserUpdateEvent events when the user list in your room changes.</li>
 <li>receivingRoomVariableUpdates - If true, you receive the initial room variable list for your room and RoomVariableUpdateEvent events when room variables in your room are created, removed, or modified.</li>
 <li>receivingUserVariableUpdates - If true, you receive the initial user variable list for new users joinin your room as well as UserVariableUpdateEvent events when new user variables are created, removed, or modified on a user.</li>
 <li>receivingVideoEvents - If true, you receive RoomUserUpdateEvent event when users in the room start or stop streaming live video to the server. The stream name is part of that event.</li>
 <li>receivingRoomAttributeUpdates - If true, you receive UpdateRoomDetailsEvent events when room properties such as capacity, description, or the password changes.</li>
 </ul>
 * 
 * Shows the most simple example of how to create a new room with all default properties.
 <pre>
package  {
        import com.electrotank.electroserver5.api.CreateRoomRequest;
        import com.electrotank.electroserver5.api.JoinRoomEvent;
        import com.electrotank.electroserver5.api.MessageType;
        import com.electrotank.electroserver5.ElectroServer;
        import com.electrotank.electroserver5.user.User;
        import com.electrotank.electroserver5.zone.Room;
        import flash.events.Event;

        public class RoomJoiner {

                private var _es:ElectroServer;

                public function initialize():void {
                        //listen for the JoinRoomEvent so we know when it happens
                        _es.engine.addEventListener(MessageType.JoinRoomEvent.name, onJoinRoomEvent);

                        //create the request and populate it with the room details
                        var crr:CreateRoomRequest = new CreateRoomRequest();
                        crr.roomName = "MyRoom";
                        crr.zoneName = "MyZone";

                        //send it
                        _es.engine.send(crr);
                }

                private function onJoinRoomEvent(e:JoinRoomEvent):void {
                        trace("room joined!");

                        //get the room you're in
                        var room:Room = _es.managerHelper.zoneManager.zoneById(e.zoneId).roomById(e.roomId);

                        //print out the users in the room
                        for each (var user:User in room.users) {
                                trace(user.userName);
                        }
                }

                public function set es(value:ElectroServer):void {
                        _es = value;
                }

        }

}
    </pre>
    Here is a thorough example that uses room variables, changes room proprties, applies join subscriptions, and uses plugins.
    <pre>
package  {
        import com.electrotank.electroserver5.api.CreateRoomRequest;
        import com.electrotank.electroserver5.api.EsObject;
        import com.electrotank.electroserver5.api.JoinRoomEvent;
        import com.electrotank.electroserver5.api.MessageType;
        import com.electrotank.electroserver5.api.PluginListEntry;
        import com.electrotank.electroserver5.api.RoomVariable;
        import com.electrotank.electroserver5.ElectroServer;
        import com.electrotank.electroserver5.user.User;
        import com.electrotank.electroserver5.zone.Room;
        import flash.events.Event;

        public class RoomJoiner {

                private var _es:ElectroServer;

                public function initialize():void {
                        //listen for the JoinRoomEvent so we know when it happens
                        _es.engine.addEventListener(MessageType.JoinRoomEvent.name, onJoinRoomEvent);

                        var crr:CreateRoomRequest = new CreateRoomRequest();
                        crr.roomName = "MyRoom";
                        crr.zoneName = "MyZone";

                        //Set a few properties
                        crr.capacity = 10;
                        crr.roomDescription = "This is my room";
                        crr.password = "open sesame";

                        //create two room variables
                        //room var 1
                        var ob1:EsObject = new EsObject();
                        ob1.setString("Test_String", "blah");
                        ob1.setInteger("Test_int", 3);

                        var rv1:RoomVariable = new RoomVariable();
                        rv1.name = "test1";
                        rv1.value = ob1;
                        rv1.locked = true;
                        rv1.persistent = false;

                        //room var 2
                        var ob2:EsObject = new EsObject();
                        ob1.setString("Test_String2", "blah2");
                        ob1.setInteger("Test_int2", 12);

                        var rv2:RoomVariable = new RoomVariable();
                        rv2.name = "test2";
                        rv2.value = ob2;
                        rv2.locked = false;
                        rv2.persistent = false;

                        //add the room variables to the request
                        crr.variables = [rv1, rv2];

                        //Create a new plugin for this room
                        var pl:PluginListEntry = new PluginListEntry();
                        pl.extensionName = "ExamplePluginExtension";
                        pl.pluginHandle = "ExamplePlugin";
                        pl.pluginName = "ExamplePlugin";

                        //add the plugin to the request
                        crr.plugins = [pl];

                        //Now configure some of the user's subscription properties
                        crr.receivingRoomAttributeUpdates = false;//Don't want to receive updates about other rooms
                        crr.receivingRoomListUpdates = false;//Don't want to receive updates about when other rooms are created or destroyed
                        crr.receivingUserListUpdates = true;//true is the default. When true you will receive user list updates for your room.

                        //send it
                        _es.engine.send(crr);
                }

                private function onJoinRoomEvent(e:JoinRoomEvent):void {
                        trace("room joined!");

                        //get the room you're in
                        var room:Room = _es.managerHelper.zoneManager.zoneById(e.zoneId).roomById(e.roomId);

                        //print out the users in the room
                        for each (var user:User in room.users) {
                                trace(user.userName);
                        }
                }

                public function set es(value:ElectroServer):void {
                        _es = value;
                }

        }

}    </pre>
 */
@interface EsCreateRoomRequest : EsRequest {
@private
	BOOL zoneName_set_;
	NSString* zoneName_;
	BOOL zoneId_set_;
	int32_t zoneId_;
	BOOL roomName_set_;
	NSString* roomName_;
	BOOL capacity_set_;
	int32_t capacity_;
	BOOL password_set_;
	NSString* password_;
	BOOL roomDescription_set_;
	NSString* roomDescription_;
	BOOL persistent_set_;
	BOOL persistent_;
	BOOL hidden_set_;
	BOOL hidden_;
	BOOL receivingRoomListUpdates_set_;
	BOOL receivingRoomListUpdates_;
	BOOL receivingRoomAttributeUpdates_set_;
	BOOL receivingRoomAttributeUpdates_;
	BOOL receivingUserListUpdates_set_;
	BOOL receivingUserListUpdates_;
	BOOL receivingUserVariableUpdates_set_;
	BOOL receivingUserVariableUpdates_;
	BOOL receivingRoomVariableUpdates_set_;
	BOOL receivingRoomVariableUpdates_;
	BOOL receivingVideoEvents_set_;
	BOOL receivingVideoEvents_;
	BOOL nonOperatorUpdateAllowed_set_;
	BOOL nonOperatorUpdateAllowed_;
	BOOL nonOperatorVariableUpdateAllowed_set_;
	BOOL nonOperatorVariableUpdateAllowed_;
	BOOL createOrJoinRoom_set_;
	BOOL createOrJoinRoom_;
	BOOL plugins_set_;
	NSMutableArray* plugins_;
	BOOL variables_set_;
	NSMutableArray* variables_;
	BOOL usingLanguageFilter_set_;
	BOOL usingLanguageFilter_;
	BOOL languageFilterSpecified_set_;
	BOOL languageFilterSpecified_;
	BOOL languageFilterName_set_;
	NSString* languageFilterName_;
	BOOL languageFilterDeliverMessageOnFailure_set_;
	BOOL languageFilterDeliverMessageOnFailure_;
	BOOL languageFilterFailuresBeforeKick_set_;
	int32_t languageFilterFailuresBeforeKick_;
	BOOL languageFilterKicksBeforeBan_set_;
	int32_t languageFilterKicksBeforeBan_;
	BOOL languageFilterBanDuration_set_;
	int32_t languageFilterBanDuration_;
	BOOL languageFilterResetAfterKick_set_;
	BOOL languageFilterResetAfterKick_;
	BOOL usingFloodingFilter_set_;
	BOOL usingFloodingFilter_;
	BOOL floodingFilterSpecified_set_;
	BOOL floodingFilterSpecified_;
	BOOL floodingFilterName_set_;
	NSString* floodingFilterName_;
	BOOL floodingFilterFailuresBeforeKick_set_;
	int32_t floodingFilterFailuresBeforeKick_;
	BOOL floodingFilterKicksBeforeBan_set_;
	int32_t floodingFilterKicksBeforeBan_;
	BOOL floodingFilterBanDuration_set_;
	int32_t floodingFilterBanDuration_;
	BOOL floodingFilterResetAfterKick_set_;
	BOOL floodingFilterResetAfterKick_;
}

/**
 * The name of the zone in which to create the new room. If a zone of that name doesn't exist, then it will be created. Either a zone name or a zone id must be specified. Usually it is the zone name.
 */
@property(strong,nonatomic) NSString* zoneName;
/**
 * The id of the zone in which you want to create the room. A zone must be specified, but it is usually done by name using the zoneName property.
 */
@property(nonatomic) int32_t zoneId;
/**
 * Sets the name of the new room. If a room of this name already exists in the specified zone, then you will be joined to that room. If you are joined to an existing room then none of the room-level properties specified here will be used. However, all of the user-subscription properies will be used.
 */
@property(strong,nonatomic) NSString* roomName;
/**
 * A room can be given a maximum number of users allowed in it at once. This property is used to do that. The default is -1, which means that there is no limit.
 */
@property(nonatomic) int32_t capacity;
/**
 * This is optional. The room can be password protected. To do that, set a password here. Users that attempt to join this room will need to use the password.
 */
@property(strong,nonatomic) NSString* password;
/**
 * This is an optional public room-level property. By setting a string value here anyone that can see this room in a room list will see the description property.
 */
@property(strong,nonatomic) NSString* roomDescription;
/**
 * The default is false. When false, a room will automatically be removed from the server when there are no more users in it. When set to true, the room will not be removed when empty. Persistent rooms should be used wisely since that can cause a memory leak by leaving dangling rooms.
 */
@property(nonatomic) BOOL persistent;
/**
 * The default is false. If set to true, then the room will not show up in the room list for the zone in which the room was created. Users can still join the room if they know the room and zone names. The hidden property of a room can be changed later using the UpdateRoomDetailsRequest.
 */
@property(nonatomic) BOOL hidden;
/**
 * The default is true. This is a user-level property used when joining a room. You will find this property in the JoinRoomRequest as well. If true, the user will receive add/remove updaes to the room list for the current zone. This is part of the ZoneUpdateEvent.
 */
@property(nonatomic) BOOL receivingRoomListUpdates;
/**
 * The default is true. If true, you will receive UpdateRoomDetailsEvent events for all rooms in your zone. That includes description, capacity, password status, and room name.
 */
@property(nonatomic) BOOL receivingRoomAttributeUpdates;
/**
 * The default is true. This is a user-level property used when joining a room. You will find this property in the JoinRoomRequest as well. If true, the user will receive updates to the user list for this room through the RoomUserUpdateEvent.
 */
@property(nonatomic) BOOL receivingUserListUpdates;
/**
 * The default is true. This is a user-level property used when joining a room. You will find this property in the JoinRoomRequest as well. If true, the user will receive UserVariableUpdateEvent events.
 */
@property(nonatomic) BOOL receivingUserVariableUpdates;
/**
 * The default is true. This is a user-level property used when joining a room. You will find this property in the JoinRoomRequest as well. If true, the user will receive RoomVariableUpdateEvent events for the newly created room.
 */
@property(nonatomic) BOOL receivingRoomVariableUpdates;
/**
 * Default is true. If true, you will receive RoomUserUpdateEvent events when users in your room start or stop publishing live streams to the server. Video is only supported in Flash clients.
 */
@property(nonatomic) BOOL receivingVideoEvents;
@property(nonatomic) BOOL nonOperatorUpdateAllowed;
@property(nonatomic) BOOL nonOperatorVariableUpdateAllowed;
/**
 * The default is true. When true, if a room with the same zone name (or zone id) and room name already exists, then you will be joined to it rather than it being created. If false, then if 
 the room already exists you will fail to create or join the room. You will get a GenericErrorEvent.
 */
@property(nonatomic) BOOL createOrJoinRoom;
/**
 * A list of PluginListEntry objects that represent the plugins that need to be created with this room.
 */
@property(strong,nonatomic) NSMutableArray* plugins;
/**
 * A list of RoomVariables to be used in the new room that is being created.
 */
@property(strong,nonatomic) NSMutableArray* variables;
/**
 * The default is false. Use this property to enable language filtering for this room. If you enable language filtering and do nothing else, then the default language filter is used. That is defined in the web-based administrator. You can also create custom language filters through the web-based administrator and use them in a room. To use a custom language filter, assign it using the languageFilterName property. 
 When a message fails language filter validation several actions can be taken. The default actions (for the default filter) are defined on the server. If you specify a custom filter then the actions are configurable on this request. The configuration allows you to control the delivery of a failed message and the punishment given to the user (kick, ban, nothing).
 */
@property(nonatomic) BOOL usingLanguageFilter;
/**
 * If a custom language filter was specified by name using languageFilterName, then this property must be manually flagged to true. The default is false.
 */
@property(nonatomic) BOOL languageFilterSpecified;
/**
 * Custom language filters can be defined and given a name through the web-based administrator. To assign a custom filter to this room specify its name here. In addition to setting this property you must also set usingLanguageFilter to true, and languageFilterSpecified to true.
 */
@property(strong,nonatomic) NSString* languageFilterName;
/**
 * This is used only if a custom language filter is being used. The default is false. If true, then a message that fails language filter validation is still deliverd to the room.
 */
@property(nonatomic) BOOL languageFilterDeliverMessageOnFailure;
/**
 * This is used only with a custom language filter. The default is 3. Use this property to set the number of times a language filter failure is allowed before the user is kicked from the room.
 */
@property(nonatomic) int32_t languageFilterFailuresBeforeKick;
/**
 * This is used with a custom language filter. The default is 3. This sets the number of times a user can be kicked due to language filter violations before that user is banned.
 */
@property(nonatomic) int32_t languageFilterKicksBeforeBan;
/**
 * This is used with a custom language filter. The default is -1, which is indefinate. If a user is kicked the number of times specified with the kicksBeforeBan property, then the user is banned from the server. The amount of time (in seconds) a user should be banned for is set with this property.
 */
@property(nonatomic) int32_t languageFilterBanDuration;
/**
 * This is used if a custom language filter has been specified. When a language filter failure occurs it is stored and associated with that user. When the amount specified by the failuresBeforeKick property is reached the user is kicked from the room. If this property is true then that number is reset to 0 when the user comes back in the room. If false, then that number is not reset.
 */
@property(nonatomic) BOOL languageFilterResetAfterKick;
/**
 * The default if false. If set to true then flood filtering is enabled for the room using the server-defined default flooding filter. You can view and edit the properties of the default flooding filter through the web-based administrator. If you want to specify a custom flooding filter then use the floodingFilterName property.
 */
@property(nonatomic) BOOL usingFloodingFilter;
/**
 * You must set to true if a custom flooding filter name has been specified. The default is false.
 */
@property(nonatomic) BOOL floodingFilterSpecified;
/**
 * The name of the custom flooding filter to use. The custom flooding filter is defined via the web-based administrator and given a name. That name is used here. In order to use a custom flooding filter in a room you must set usingFloodingFilter to true, and floodingFilterSpecified to true.
 */
@property(strong,nonatomic) NSString* floodingFilterName;
/**
 * This property is used if a custom flooding filter has been specified. The default is 1. When flooding has been detected it counts as 1 failure. When a user has caused the number of failures specified by this property that user is then kicked from the room.
 */
@property(nonatomic) int32_t floodingFilterFailuresBeforeKick;
/**
 * This property is used if a custom flooding filter has been specified. The default is 3. The amount of times a user is kicked due to the flooding filter is tracked. When the user reaches the number specified here that user is banned. The duration of that ban is set with vai floodingFilterBanDuration.
 */
@property(nonatomic) int32_t floodingFilterKicksBeforeBan;
/**
 * This property is used if a custom flooding filter has been specified. The default is -1, which is an indefinate ban (until server reboot). The ban duration can be set with this property. The value is in seconds. A banned user cannot log back in.
 */
@property(nonatomic) int32_t floodingFilterBanDuration;
/**
 * If using a custom flooding filter then this property is used. The default is false. The user is kicked after a number of flood failures has been reached. That number is reset after kick if this property is set to true. You can configure the number of failures before kick via floodingFilterFailuresBeforeKick.
 */
@property(nonatomic) BOOL floodingFilterResetAfterKick;

- (id) init;
- (id) initWithThriftObject: (id) thriftObject;
- (void) fromThrift: (id) thriftObject;
- (ThriftCreateRoomRequest*) toThrift;
- (id) newThrift;
@end