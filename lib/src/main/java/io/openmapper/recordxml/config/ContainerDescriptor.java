package io.openmapper.recordxml.config;

/*
SINGLE
Simple:
    <Parent Field="entry"/>
    <Parent value="entry"/>
    <Parent>entry</Parent>
    <Parent><Field value="entry"/></Parent>
    <Parent><Field>entry</Field></Parent>

 Polymorphic Simple:
    <Parent type="int" Field="entry"/>
    <Parent type="int" value="entry"/>
    <Parent type="int"><Field value="entry"/></Parent>
    <Parent type="int"><Field>entry</Field></Parent>

    <Parent><Field type="int" value="entry"/></Parent>
    <Parent><Field type="int">entry<Field/></Parent>

    <Parent><int value="entry"/></Parent>
    <Parent><int>entry<int/></Parent>

    <Parent><Field><int value="entry"/><Field/></Parent>
    <Parent><Field><int>entry<int/><Field/></Parent>

Complex:
    <Parent><Value entry/></Parent>
    <Parent><Field entry/></Parent>
    <Parent><Field><Value entry/></Field></Parent>

Polymorphic Complex:
    <Parent type="class"><Field entry/></Parent>
    <Parent type="class"><Value entry/></Parent>
    <Parent type="class"><Field><Value entry/></Field></Parent>

    <Parent><Field type="class" entry/></Parent>
    <Parent><Field type="class"><Value entry/></Field></Parent>

    <Parent><Value type="class" entry/></Parent>
    <Parent><Field><Value type="class" entry/></Field></Parent>

    <Parent><Type entry/></Parent>
    <Parent><Field><Type entry/></Field></Parent>

PLURAL
Simple:
    <Parent>
        <Field key="1" value="entry1"/>
        <Field key="2" value="entry2"/>
    </Parent>
    <Parent>
        <Field key="1">entry1</Field>
        <Field key="2">entry2</Field>
    </Parent>

Polymorphic Simple:
    <Parent type="int">
        <Field key="1" value="entry1"/>
        <Field key="2" value="entry2"/>
    </Parent>
    <Parent type="int">
        <Field key="1">entry1</Field>
        <Field key="2">entry2</Field>
    </Parent>

    <Parent>
        <Field type="int" key="1" value="entry1"/>
        <Field type="int" key="2" value="entry2"/>
    </Parent>
    <Parent>
        <Field type="int" key="1">entry1</Field>
        <Field type="int" key="2">entry2</Field>
    </Parent>
    <Parent>
        <int key="1" value="entry1"/>
        <int key="2" value="entry2"/>
    </Parent>
    <Parent>
        <int key="1">entry1</int>
        <int key="2">entry2</int>
    </Parent>
    <Parent>
        <Field>
            <int key="1" value="entry1"/>
            <int key="2" value="entry2"/>
        </Field>
    </Parent>
    <Parent>
        <Field>
            <int key="1">entry1</int>
            <int key="2">entry2</int>
        </Field>
    </Parent>
    <Parent>
        <Field key="1"><int value="entry1"/></Field>
        <Field key="2"><int value="entry2"/></Field>
    </Parent>
    <Parent>
        <Field key="1"><int>entry1</int></Field>
        <Field key="2"><int>entry2</int></Field>
    </Parent>

Complex:
    <Parent>
        <Value key="1" entry/>
        <Value key="2" entry/>
    </Parent>
    <Parent>
        <Field key="1" entry/>
        <Field key="2" entry/>
    </Parent>
    <Parent>
        <Field>
            <Value key="1" entry/>
            <Value key="2" entry/>
        </Field>
    </Parent>
    <Parent>
        <Field key="1"><Value entry/></Field>
        <Field key="2"><Value entry/></Field>
    </Parent>

Polymorphic Complex:
    <Parent type="class">
        <Value key="1" entry/>
        <Value key="2" entry/>
    </Parent>
    <Parent>
        <Value key="1" type="class1" entry/>
        <Value key="2" type="class2" entry/>
    </Parent>
    <Parent>
        <Class1 key="1" entry/>
        <Class2 key="2" entry/>
    </Parent>
    <Parent type="class" >
        <Field key="1" entry/>
        <Field key="2" entry/>
    </Parent>
    <Parent>
        <Field key="1" type="class" entry/>
        <Field key="2" type="class" entry/>
    </Parent>
    <Parent>
        <Field>
            <Value key="1" entry/>
            <Value key="2" entry/>
        </Field>
    </Parent>
    <Parent>
        <Field>
            <Value key="1" entry/>
            <Value key="2" entry/>
        </Field>
    </Parent>
    <Parent>
        <Field>
            <Value key="1" entry/>
            <Value key="2" entry/>
        </Field>
    </Parent>
    <Parent>
        <Field key="1"><Value entry/></Field>
        <Field key="2"><Value entry/></Field>
    </Parent>


    named attribute on parent:  <Parent type="int" Field="entry"/>
                                <Parent type="int" value="entry"/>
                                <Parent type="int"><Field value="entry"/></Parent>
                                <Parent type="int"><Field>entry</Field></Parent>

    named attribute on field:   <Parent><Field type="int" value="entry"/></Parent>
                                <Parent><Field type="int">entry<Field/></Parent>

    named element on parent:    <Parent><int value="entry"/></Parent>
                                <Parent><int>entry<int/></Parent>

    named element on field:    <Parent><Field><int value="entry"/><Field/></Parent>
                               <Parent><Field><int>entry<int/><Field/></Parent>

Complex:
    <Parent><Value entry/></Parent>
    <Parent><Field entry/></Parent>
    <Parent><Field><Value entry/></Field></Parent>

Polymorphic Complex:
    <Parent type="class"><Field entry/></Parent>
    <Parent type="class"><Value entry/></Parent>
    <Parent type="class"><Field><Value entry/></Field></Parent>

    <Parent><Field type="class" entry/></Parent>
    <Parent><Field type="class"><Value entry/></Field></Parent>

    <Parent><Value type="class" entry/></Parent>
    <Parent><Field><Value type="class" entry/></Field></Parent>

    <Parent><Type entry/></Parent>
    <Parent><Field><Type entry/></Field></Parent>

 */

/**
 * 1. Option<String> -> <?Parent? value="entry" />
 * 1. Option<String> -> <?Parent? ?Field?="entry" />
 * 1. Option<String> -> <?Parent?>entry</?Parent?>
 * 1. Option<String> -> <?Parent?><?Field? value="entry"/></?Parent?>
 * 1. Option<String> -> <?Parent?><?Field?>entry</?Field?></?Parent?>
 * <p>
 * 1. Option<Number> -> <?Parent? int="entry" />
 * 1. Option<Number> -> <?Parent? type="int" value="entry" />
 * 1. Option<Number> -> <?Parent? type="int" ?Field?="entry" />
 * <p>
 * 1. Option<Number> -> <?Parent? type="int">entry</?Parent?>
 * 1. Option<Number> -> <?Parent?><int>entry</int></?Parent?>
 * 1. Option<Number> -> <?Parent?><int value="entry"/></?Parent?>
 * <p>
 * 1. Option<Number> -> <?Parent?><?Field?  int="entry"/></?Parent?>
 * 1. Option<Number> -> <?Parent? type="int" value="entry" />
 * 1. Option<Number> -> <?Parent? type="int" ?Field?="entry" />
 * <p>
 * <p>
 * 1. Option<Number> -> <?Parent? type="int">entry</?Parent?>
 * 1. Option<Number> -> <?Parent? type="int"><?Field? value="entry"/></?Parent?>
 * 1. Option<Number> -> <?Parent?><?Field? int="entry"/></?Parent?>
 * 1. Option<Number> -> <?Parent?><?Field? type="int" value="entry"/></?Parent?>
 * 1. Option<Number> -> <?Parent?><int>entry<int/></?Parent?>
 * <p>
 * 1. Option<Number> -> <?Parent? ?Field?="entry" Type="int" />
 * 1. Option<Number> -> <?Parent? Type="int">entry</?Parent?>
 * <p>
 * 1. Option<Number> -> <?Parent?><?Field? int="entry"><?Parent?>
 * 1. Option<Number> -> <?Parent?><?Field?><int>entry</int><?Field?></?Parent?>
 * 1. Option<Number> -> <?Parent?><int>entry</int></?Parent?>
 * 1. Option<Number> -> <?Entry? Type="int">entry</?Entry?>
 * 1. Option<Number> -> <?Parent? Type="int"><?Entry?>entry</?Entry?></?Parent?>
 * <p>
 * 2. Seq<String> -> <???>entry</???><???>entry</???>
 * 3. Seq<Number> -> <???><int>entry<int/><dbl>entry<dbl/></???>
 * 4. Seq<Number> -> <??? type="int">entry</???><??? type="dbl">entry</???>
 * 5. Seq<Number> -> <??? type="int">entry</???><??? type="dbl">entry</???>
 * 4. Map<String></String>
 */
public non-sealed interface ContainerDescriptor extends Descriptor {

}
