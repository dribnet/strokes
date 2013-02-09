// for mrhyde testing, we first setup a dummy javascript library
DummyLib = {
  wrapArgs0and2: function(a,b,c) {
    return [a, b, c];
  },
  wrapReturnArgsIntoArray: function(a,b,c) {
    return [a, b, c];
  },
  wrapReturnArgsIntoObject: function(a,b,c) {
    return {a: a, b: b, c: c};
  },
  wrapCall0on1: function(a,b,c) {
    return a(b);
  },
  wrapArraysInAndOut: function(a,b,c) {
    arrays = [];
    if (a instanceof Array) {
      arrays.push(a);
    }
    if (b instanceof Array) {
      arrays.push(b);
    }
    if (c instanceof Array) {
      arrays.push(c);
    }
    return arrays;
  },
  zeroOutFirstArrayElement: function(a) {
    a[0] = 0;
    return [a, a[0]];
  },
  zeroOutMapKeyOne: function(a) {
    a.one = 0;
    return [a, a.one];
  },
  zeroOutMapKeyTen: function(a) {
    a.ten = 0;
    return [a, a.ten];
  },
}
var p = {
  node: "yes",
  parent: null,
  children: [
    { node: "yes",
      parent: null,
      children: []
    }
  ]
};
p.children[0].parent = p;
