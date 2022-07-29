import 'dart:convert';

class BeaconModel {
  final String? mac;
  final int rssi = 0;
  final String? name;
  final String? state;
  BeaconModel({
    this.mac,
    this.name,
    this.state,
  });

  BeaconModel copyWith({
    String? mac,
    String? name,
    String? state,
  }) {
    return BeaconModel(
      mac: mac ?? this.mac,
      name: name ?? this.name,
      state: state ?? this.state,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'mac': mac,
      'name': name,
      'state': state,
    };
  }

  factory BeaconModel.fromMap(Map<String, dynamic> map) {
    return BeaconModel(
      mac: map['mac'],
      name: map['name'],
      state: map['state'],
    );
  }

  String toJson() => json.encode(toMap());

  factory BeaconModel.fromJson(String source) =>
      BeaconModel.fromMap(json.decode(source));

  @override
  String toString() => 'BeaconModel(mac: $mac, name: $name, state: $state)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is BeaconModel &&
        other.mac == mac &&
        other.name == name &&
        other.state == state;
  }

  @override
  int get hashCode => mac.hashCode ^ name.hashCode ^ state.hashCode;
}
